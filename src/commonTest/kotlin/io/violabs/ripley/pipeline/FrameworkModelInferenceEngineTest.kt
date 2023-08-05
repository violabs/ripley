package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.*
import io.violabs.ripley.testSuite.FakeModel
import io.violabs.ripley.testSuite.testEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.test.assertTrue

class FrameworkModelInferenceEngineTest {
    private val pretrained = FakeModel()

    private val frameworkInferenceEngine = FakeFrameworkInferenceEngine(FakeModel::class)

    private val frameworkValidator = object : IFrameworkValidator {
        var called: Boolean = false

        fun reset() {
            called = false
        }

        override fun requireActiveFramework() {
            called = true
        }
    }

    private val modelInferenceEngine = FakeModelInferenceEngine { pretrained }

    private val engine = FrameworkModelInferenceEngine(
        frameworkInferenceEngine,
        frameworkValidator,
        modelInferenceEngine
    )

    @Test
    fun inferFrameworkLoadModel_will_throw_an_error_if_not_a_string_or_matching_class() {
        assertThrows<Exception> { engine.inferFrameworkLoadModel(FakeModel::class, 1) }
    }

    @Test
    fun inferFrameworkLoadModel_will_process_framework_if_model_already_defined() {
        frameworkValidator.reset()

        frameworkInferenceEngine.expected = { model, _ ->
            assert(model == pretrained)
            FrameworkName.PYTORCH
        }

        val (framework, model) = engine.inferFrameworkLoadModel(FakeModel::class, pretrained)

        assertTrue(frameworkValidator.called)
        testEquals(framework, FrameworkName.PYTORCH)
        testEquals(model, pretrained)
    }

    @Test
    fun inferFrameworkLoadModel_will_process_framework_if_model_and_framework_already_defined() {
        frameworkValidator.reset()

        frameworkInferenceEngine.expected = { model, framework ->
            assert(model == pretrained)
            assert(framework == FrameworkName.PYTORCH)
            framework!!
        }

        val (framework, model) = engine.inferFrameworkLoadModel(
            FakeModel::class,
            pretrained,
            framework = FrameworkName.PYTORCH
        )

        assertTrue(frameworkValidator.called)
        testEquals(framework, FrameworkName.PYTORCH)
        testEquals(model, pretrained)
    }

    @Test
    fun inferFrameworkLoadModel_will_process_framework_if_model_is_a_string_with_minimal_input() {
        frameworkValidator.reset()

        frameworkInferenceEngine.expected = { model, _ ->
            assert(model == pretrained)
            FrameworkName.PYTORCH
        }

        val kwargs = ModelKwargs()

        val (framework, model) = engine.inferFrameworkLoadModel(
            FakeModel::class,
            "model",
            modelKwargs = kwargs
        )

        assertTrue(frameworkValidator.called)
        testEquals(framework, FrameworkName.PYTORCH)
        testEquals(model, pretrained)
        testEquals(kwargs.fromPipeline, "")
    }

    @Test
    fun inferFrameworkLoadModel_will_process_framework_if_model_is_a_string_with_maximum_input() {
        frameworkValidator.reset()

        frameworkInferenceEngine.expected = { model, framework ->
            testEquals(model, pretrained)
            testEquals(framework, FrameworkName.PYTORCH)
            framework!!
        }

        val kwargs = ModelKwargs()

        val (framework, model) = engine.inferFrameworkLoadModel(
            FakeModel::class,
            "model",
            modelClasses = mapOf(FrameworkName.PYTORCH to { FakeModel() }),
            task = "task",
            framework = FrameworkName.PYTORCH,
            modelKwargs = kwargs
        )

        assertTrue(frameworkValidator.called)
        testEquals(framework, FrameworkName.PYTORCH)
        testEquals(model, pretrained)
        testEquals(kwargs.fromPipeline, "task")
    }
}

class FakeModelInferenceEngine(var expected: () -> IPreTrainedModel) : IModelInferenceEngine {
    override fun <T : IPreTrainedModel> inferModel(
        clazz: KClass<T>,
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>?,
        framework: FrameworkName?,
        modelKwargs: ModelKwargs
    ): T {
        val expected = expected()
        return if (clazz.isInstance(expected))
            clazz.cast(expected)
        else
            throw Exception("You must set the test expected! It must be the same type as you expect returned.")
    }
}

class FakeFrameworkInferenceEngine<U : IPreTrainedModel>(
    val clazz: KClass<U>,
    var expected: ((U, FrameworkName?) -> FrameworkName)? = null
) : IFrameworkInferenceEngine {
    override fun <T : IPreTrainedModel> inferFramework(model: T, framework: FrameworkName?): FrameworkName {
        val castModel: U = if (clazz.isInstance(model)) clazz.cast(model)
        else throw Exception("Model must be the same class as expected - model=${model::class}, class=${clazz}")

        return expected?.invoke(castModel, framework)
            ?: throw Exception("You must set the test expected! It must be the same type as you expect returned.")
    }
}