package io.violabs.ripley.model.albert

import io.violabs.ripley.common.*
import io.violabs.ripley.config.SerializationConfig
import io.violabs.ripley.domain.Kwargs
import kotlinx.serialization.Serializable
import mu.two.KotlinLogging

enum class AlbertPretrainedConfigOptions(
    val url: String
) {
    BASE_V1("https://huggingface.co/albert-base-v1/resolve/main/config.json"),
    LARGE_V1("https://huggingface.co/albert-large-v1/resolve/main/config.json"),
    XLARGE_V1("https://huggingface.co/albert-xlarge-v1/resolve/main/config.json"),
    XXLARGE_V1("https://huggingface.co/albert-xxlarge-v1/resolve/main/config.json"),
    BASE_V2("https://huggingface.co/albert-base-v2/resolve/main/config.json"),
    LARGE_V2("https://huggingface.co/albert-large-v2/resolve/main/config.json"),
    XLARGE_V2("https://huggingface.co/albert-xlarge-v2/resolve/main/config.json"),
    XXLARGE_V2("https://huggingface.co/albert-xxlarge-v2/resolve/main/config.json")
}

@Serializable
abstract class PretrainedConfig(
    open val padTokenId: Long? = null,
    open val bosTokenId: Long? = null,
    open val eosTokenId: Long? = null,
    open val kwargs: Kwargs? = null
) {
    abstract val modelType: String

    var numberOfLabels: Int? = null
    val problemType: ProblemType? = determineProblemType(kwargs)

    init {
        val id2Label = kwargs?.id2Label
        if (id2Label == null) {
            numberOfLabels = kwargs?.numberOfLabels ?: 2
        } else {
            checkLabelAmount(id2Label)
        }

        kwargs?.xlaDevice?.also {
            LOGGER.warn {
                """The `xla_device` argument has been deprecated in v4.4.0 of Transformers. 
                   It is ignored and you can safely remove it from your `config.json` file.""".trimIndent()
            }
        }

        if (kwargs?.gradientCheckpointing == true) {
            LOGGER.warn {
                """Passing `gradient_checkpointing` to a config initialization is deprecated and will be removed in v5
                   Transformers. Using `model.gradient_checkpointing_enable()` instead, or if you are using the 
                   `Trainer` API, pass `gradient_checkpointing=True` in your `TrainingArguments`.
                """.trimIndent()
            }
        }

        // todo add torch
        //        if self.torch_dtype is not None and isinstance(self.torch_dtype, str):
        //            # we will start using self.torch_dtype in v5, but to be consistent with
        //            # from_pretrained's torch_dtype arg convert it to an actual torch.dtype object
        //            if is_torch_available():
        //                import torch
        //
        //                self.torch_dtype = getattr(torch, self.torch_dtype)


    }

    private fun determineProblemType(kwargs: Kwargs?): ProblemType? {
        val kwargProblemType: ProblemType? = kwargs?.problemType

        if (ALLOWED_PROBLEM_TYPES.contains(kwargProblemType)) return kwargProblemType

        else throw IllegalArgumentException(
            """The config parameter `problem_type` was not understood: received $kwargProblemType 
               but only $ALLOWED_PROBLEM_TYPES are valid.
            """.trimIndent()
        )
    }

    private fun checkLabelAmount(id2Label: Map<*, *>) {
        val numberOfLabels: Int? = kwargs?.numberOfLabels

        if (numberOfLabels != null && id2Label.size != numberOfLabels) {
            LOGGER.warn {
                """
                    | You passed along `numLabels=$numberOfLabels` with an incompatible id to label map: 
                    | $id2Label. The number of labels will be overwritten to $numberOfLabels.
                """.trimMargin()
            }
        }
    }

    fun useReturnDictionary(kwargs: Kwargs?): Boolean = kwargs?.returnDictionary == true && !kwargs.torchScript

    fun savePretrained(
        saveDirectory: String,
        pushToHub: Boolean = false,
        kwargs: Kwargs
    ) {
        setTokenInKwargs(kwargs)

        // todo: check file directory [configuration_utils:440]
        //         if os.path.isfile(save_directory):
        //            raise AssertionError(f"Provided path ({save_directory}) should be a directory, not a file")
//
//        os.makedirs(save_directory, exist_ok=True)

        // push to hub
        //         if push_to_hub:
        //            commit_message = kwargs.pop("commit_message", None)
        //            repo_id = kwargs.pop("repo_id", save_directory.split(os.path.sep)[-1])
        //            repo_id = self._create_repo(repo_id, **kwargs)
        //            files_timestamps = self._get_files_timestamps(save_directory)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}

        val ALLOWED_PROBLEM_TYPES: List<ProblemType> = listOf(
            ProblemType.REGRESSION,
            ProblemType.SINGLE_LABEL_CLASSIFICATION,
            ProblemType.MULTI_LABEL_CLASSIFICATION
        )

        private fun logAuthTokenDeprecation() {
            LOGGER.warn {
                "The `use_auth_token` argument is deprecated and will be removed in v5 of Transformers."
            }
        }

        private fun checkTokenExistence(token: String?) {
            if (token == null) return

            throw IllegalArgumentException(
                "`token` and `use_auth_token` are both specified. Please set only the argument `token`."
            )
        }

        fun setTokenInKwargs(kwargs: Kwargs, inputToken: String? = null) {
            val token: String? = inputToken ?: kwargs.token
            val authToken: String? = kwargs.authToken

            val tokenToUse: String =
                authToken
                    ?.also { logAuthTokenDeprecation() }
                    ?.also { checkTokenExistence(token) }
                    ?: token
                    ?: return

            kwargs.token = tokenToUse
        }
    }
}

@Serializable
class AlbertConfig(
    val vocabSize: Long? = 30_000,
    val embeddingSize: Long? = 128,
    val hiddenSize: Long? = 4096,
    val numberOfHiddenLayers: Int? = 12,
    val numberOfHiddenGroups: Int? = 1,
    val numberOfAttentionHeads: Int? = 64,
    val intermediateSize: Long? = 16384,
    val innerGroupNumber: Int? = 1,
    val hiddenAct: HiddenAct? = HiddenAct.GELU_NEW,
    val hiddenDropoutProbability: Double? = 0.0,
    val maxPositionEmbeddings: Long? = 512,
    val typeVocabSize: Long? = 2,
    val initializerRange: Double? = 0.02,
    val layerNormEpsilon: Double? = 1e-12,
    val classifierDropoutProbability: Double? = 0.1,
    val positionEmbeddingType: Position? = Position.ABSOLUTE,
    private val _padTokenId: Long? = 0,
    private val _bosTokenId: Long? = 2,
    private val _eosTokenId: Long? = 3,
    private val _kwargs: Kwargs? = Kwargs()
) : PretrainedConfig(_padTokenId, _bosTokenId, _eosTokenId, _kwargs) {
    override val modelType: String = "albert"

    fun maxPositionEmbeddingsOrDefault(): Long = maxPositionEmbeddings ?: 512

    companion object {
        fun fromJsonFile(jsonFile: String?): AlbertConfig {
            val jsonString: String = jsonFile ?: return AlbertConfig()

            return SerializationConfig.objectMapper.decodeFromString(serializer(), jsonString)
        }
    }
}

@Serializable
enum class HiddenAct {
    GELU_NEW;

    override fun toString(): String = name.lowercase()
}

@Serializable
enum class Position {
    ABSOLUTE;

    override fun toString(): String = name.lowercase()
}

@Serializable
enum class ProblemType {
    REGRESSION,
    SINGLE_LABEL_CLASSIFICATION,
    MULTI_LABEL_CLASSIFICATION;

    override fun toString(): String = name.lowercase()

    companion object {
        fun fromString(value: String): ProblemType? = values().find { it.name.equals(value, ignoreCase = true) }
    }
}