package io.violabs.ripley.model.albert

import io.violabs.ripley.common.*
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

abstract class PretrainedConfig(
    padTokenId: Long? = null,
    bosTokenId: Long? = null,
    eosTokenId: Long? = null,
    val kwargs: Kwargs
) {
    abstract val modelType: String

    val returnDictionary: Boolean = kwargs.extractDefault("return_map", true)
    val outputHiddenStates: Boolean = kwargs.extractDefault("output_hidden_states", false)
    val outputAttentions: Boolean = kwargs.extractDefault("output_attentions", false)
    val torchScript: Boolean = kwargs.extractDefault("torchscript", false)
    val torchDataType: String? = kwargs.extract("torch_dtype")
    val useBFloat16: Boolean = kwargs.extractDefault("use_bfloat16", false)
    val tfLegacyLoss: Boolean = kwargs.extractDefault("tf_legacy_loss", false)
    val prunedHeads: Map<Int, List<Int>> = kwargs.remove("pruned_heads").parseAsMultiValueMap()
    val tieWordEmbeddings: Boolean = kwargs.extractDefault("tie_word_embeddings", true)
    val isEncoderDecoder: Boolean = kwargs.extractDefault("is_encoder_decoder", false)
    val isDecoder: Boolean = kwargs.extractDefault("is_decoder", false)
    val addCrossAttention: Boolean = kwargs.extractDefault("add_cross_attention", false)
    val tieEncoderDecoder: Boolean = kwargs.extractDefault("tie_encoder_decoder", false)
    val crossAttentionHiddenSize: Long? = kwargs.extract("cross_attention_hidden_size")
    val maxLength: Short = kwargs.extractDefault("max_length", 20)
    val minLength: Short = kwargs.extractDefault("min_length", 0)
    val doSample: Boolean = kwargs.extractDefault("do_sample", false)
    val earlyStopping: Boolean = kwargs.extractDefault("early_stopping", false)
    val numberOfBeams: Int = kwargs.extractDefault("num_beams", 1)
    val numberOfBeamGroups: Int = kwargs.extractDefault("num_beam_groups", 1)
    val diversityPenalty: Double = kwargs.extractDefault("diversity_penalty", 0.0)
    val temperature: Double = kwargs.extractDefault("temperature", 1.0)
    val topK: Int = kwargs.extractDefault("top_k", 50)
    val topP: Double = kwargs.extractDefault("top_p", 1.0)
    val typicalP: Double = kwargs.extractDefault("typical_p", 1.0)
    val repetitionPenalty: Double = kwargs.extractDefault("repetition_penalty", 1.0)
    val lengthPenalty: Double = kwargs.extractDefault("length_penalty", 1.0)
    val noRepeatNGramSize: Int = kwargs.extractDefault("no_repeat_ngram_size", 0)
    val badWordsIds: Matrix<Int> = kwargs.remove("bad_words_ids").parseAsMatrix()
    val numReturnSequences: Int = kwargs.extractDefault("num_return_sequences", 1)
    val chunkSizeFeedForward: Int = kwargs.extractDefault("chunk_size_feed_forward", 0)
    val outputScores: Boolean = kwargs.extractDefault("output_scores", false)
    val returnDictionaryInGenerated: Boolean = kwargs.extractDefault("return_dict_in_generate", false)
    val forcedBosTokenId: Long? = kwargs.extract("forced_bos_token_id")
    val forcedEosTokenId: Long? = kwargs.extract("forced_eos_token_id")
    val removeInvalidValues: Boolean = kwargs.extractDefault("remove_invalid_values", false)
    val exponentialDecayLengthPenalty: Double? = kwargs.extract("exponential_decay_length_penalty")
    val suppressTokens: Any? = kwargs.remove("suppress_tokens")
    val beginSuppressTokens: Any? = kwargs.remove("begin_suppress_tokens")
    val architecture: Any? = kwargs.remove("architectures")
    val fineTuningTask: String? = kwargs.extract("finetuning_task")
    var id2Label: Map<Int, String>? = kwargs.remove("id2label").parseAsMap()
    val label2Id: Map<String, Int> = kwargs.remove("label2id").parseAsMap()
    var numberOfLabels: Int? = null
    val tokenizerClass: String? = kwargs.extract("tokenizer_class")
    val prefix: String? = kwargs.extract("prefix")
    val bosTokenId: Long? = bosTokenId ?: kwargs.extract("bos_token_id")
    val eosTokenId: Long? = eosTokenId ?: kwargs.extract("eos_token_id")
    val padTokenId: Long? = padTokenId ?: kwargs.extract("pad_token_id")
    val sepTokenId: Long? = kwargs.extract("sep_token_id")
    val decoderStartTokenId: Long? = kwargs.extract("decoder_start_token_id")
    val taskSpecificParameters: Map<String, Any> = kwargs.remove("task_specific_params").parseAsMap()
    val problemType: ProblemType? = determineProblemType(kwargs)
    val nameOrPath: String = kwargs.extractDefault("name_or_path","")
    val commitHash: String? = kwargs.extract("commit_hash")

    init {
        if (id2Label == null) {
            numberOfLabels = kwargs.extractDefault("num_labels", 2)
        } else {
            checkLabelAmount(id2Label!!)
        }

        kwargs.remove("xla_device")?.also {
            LOGGER.warn {
                """The `xla_device` argument has been deprecated in v4.4.0 of Transformers. 
                   It is ignored and you can safely remove it from your `config.json` file.""".trimIndent()
            }
        }

        if (kwargs.extractDefault("gradient_checkpointing", false)) {
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

    private fun determineProblemType(kwargs: Kwargs): ProblemType? {
        val kwargProblemType: ProblemType? =
            kwargs
                .extract<String>("problem_type")
                ?.let(ProblemType::fromString)

        if (ALLOWED_PROBLEM_TYPES.contains(kwargProblemType)) return kwargProblemType

        else throw IllegalArgumentException(
            """The config parameter `problem_type` was not understood: received $kwargProblemType 
               but only $ALLOWED_PROBLEM_TYPES are valid.
            """.trimIndent()
        )
    }

    private fun checkLabelAmount(id2Label: Map<*, *>) {
        val numberOfLabels: Int? = kwargs.extract<Int>("num_labels")

        if (numberOfLabels != null && id2Label.size != numberOfLabels) {
            LOGGER.warn {
                """
                    | You passed along `numLabels=$numberOfLabels` with an incompatible id to label map: 
                    | $id2Label. The number of labels will be overwritten to $numberOfLabels.
                """.trimMargin()
            }
        }
    }

    fun useReturnDictionary(): Boolean = returnDictionary && !torchScript

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
            val token: String? = inputToken ?: kwargs.remove("token") as? String
            val authToken: String? = kwargs.remove("auth_token") as? String

            val tokenToUse: String =
                authToken
                    ?.also { logAuthTokenDeprecation() }
                    ?.also { checkTokenExistence(token) }
                    ?: token
                    ?: return

            kwargs["token"] = tokenToUse
        }
    }
}

class AlbertConfiguration(
    val vocabSize: Long = 30_000,
    val embeddingSize: Long = 128,
    val hiddenSize: Long = 4096,
    val numberOfHiddenLayers: Int = 12,
    val numberOfHiddenGroups: Int = 1,
    val numberOfAttentionHeads: Int = 64,
    val intermediateSize: Long = 16384,
    val innerGroupNumber: Int = 1,
    val hiddenAct: HiddenAct = HiddenAct.GELU_NEW,
    val hiddenDropoutProbability: Double = 0.0,
    val maxPositionEmbeddings: Long = 512,
    val typeVocabSize: Long = 2,
    val initializerRange: Double = 0.02,
    val layerNormEpsilon: Double = 1e-12,
    val classifierDropoutProbability: Double = 0.1,
    val positionEmbeddingType: Position = Position.ABSOLUTE,
    padTokenId: Long = 0,
    bosTokenId: Long = 2,
    eosTokenId: Long = 3,
    kwargs: Kwargs = Kwargs(mutableSetOf(), mutableSetOf(), mutableSetOf())
) : PretrainedConfig(padTokenId, bosTokenId, eosTokenId, kwargs) {
    override val modelType: String = "albert"
}

enum class HiddenAct {
    GELU_NEW;

    override fun toString(): String = name.lowercase()
}

enum class Position {
    ABSOLUTE;

    override fun toString(): String = name.lowercase()
}

enum class ProblemType {
    REGRESSION,
    SINGLE_LABEL_CLASSIFICATION,
    MULTI_LABEL_CLASSIFICATION;

    override fun toString(): String = name.lowercase()

    companion object {
        fun fromString(value: String): ProblemType? = entries.find { it.name.equals(value, ignoreCase = true) }
    }
}