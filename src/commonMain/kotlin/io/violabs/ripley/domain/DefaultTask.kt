package io.violabs.ripley.domain

class DefaultTask(
    override val name: String?,
    override var pytorch: List<String>? = null,
    override var tensorflow: List<String>? = null,
    override var default: Map<Any, Map<FrameworkName, List<IModelBuilderOption>>>? = null
) : ITask