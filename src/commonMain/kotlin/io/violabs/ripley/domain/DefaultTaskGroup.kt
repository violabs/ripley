package io.violabs.ripley.domain

class DefaultTaskGroup(
    override var normalizedTask: ITask? = null,
    override var targetedTask: IAcceptedTask? = null,
    override var taskOptions: Any? = null
) : ITaskGroup