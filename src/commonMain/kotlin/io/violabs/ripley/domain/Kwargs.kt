package io.violabs.ripley.domain

import io.violabs.ripley.common.Matrix
import io.violabs.ripley.model.albert.ProblemType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
class Kwargs @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("return_dict")
    val returnDictionary: Boolean? = true,
    @JsonNames("output_hidden_states")
    val outputHiddenStates: Boolean = false,
    @JsonNames("output_attentions")
    val outputAttentions: Boolean = false,
    @JsonNames("torchscript")
    val torchScript: Boolean = false,
    @JsonNames("torch_dtype")
    val torchDataType: String? = "torch_dtype",
    @JsonNames("use_bfloat16")
    val useBFloat16: Boolean = false,
    @JsonNames("tf_legacy_loss")
    val tfLegacyLoss: Boolean = false,
    @JsonNames("pruned_heads")
    val prunedHeads: Map<Int, List<Int>> = emptyMap(),
    @JsonNames("tie_word_embeddings")
    val tieWordEmbeddings: Boolean = true,
    @JsonNames("is_encoder_decoder")
    val isEncoderDecoder: Boolean = false,
    @JsonNames("is_decoder")
    val isDecoder: Boolean = false,
    @JsonNames("add_cross_attention")
    val addCrossAttention: Boolean = false,
    @JsonNames("tie_encoder_decoder")
    val tieEncoderDecoder: Boolean = false,
    @JsonNames("cross_attention_hidden_size")
    val crossAttentionHiddenSize: Long? = null,
    @JsonNames("max_length")
    val maxLength: Short = 20,
    @JsonNames("min_length")
    val minLength: Short = 0,
    @JsonNames("do_sample")
    val doSample: Boolean = false,
    @JsonNames("early_stopping")
    val earlyStopping: Boolean = false,
    @JsonNames("num_beams")
    val numberOfBeams: Int = 1,
    @JsonNames("num_beam_groups")
    val numberOfBeamGroups: Int = 1,
    @JsonNames("diversity_penalty")
    val diversityPenalty: Double = 0.0,
    @JsonNames("temperature")
    val temperature: Double = 1.0,
    @JsonNames("top_k")
    val topK: Int = 50,
    @JsonNames("top_p")
    val topP: Double = 1.0,
    @JsonNames("typical_p")
    val typicalP: Double = 1.0,
    @JsonNames("repetition_penalty")
    val repetitionPenalty: Double = 1.0,
    @JsonNames("length_penalty")
    val lengthPenalty: Double = 1.0,
    @JsonNames("no_repeat_ngram_size")
    val noRepeatNGramSize: Int = 0,
    @JsonNames("bad_words_ids")
    val badWordsIds: Matrix<Int> = emptyList(),
    @JsonNames("num_return_sequences")
    val numReturnSequences: Int = 1,
    @JsonNames("chunk_size_feed_forward")
    val chunkSizeFeedForward: Int = 0,
    @JsonNames("output_scores")
    val outputScores: Boolean = false,
    @JsonNames("return_dict_in_generate")
    val returnDictionaryInGenerated: Boolean = false,
    @JsonNames("forced_bos_token_id")
    val forcedBosTokenId: Long? = null,
    @JsonNames("forced_eos_token_id")
    val forcedEosTokenId: Long? = null,
    @JsonNames("remove_invalid_values")
    val removeInvalidValues: Boolean = false,
    @JsonNames("exponential_decay_length_penalty")
    val exponentialDecayLengthPenalty: Double? = null,
    @JsonNames("suppress_tokens")
    val suppressTokens: Map<String, String>? = null,
    @JsonNames("begin_suppress_tokens")
    val beginSuppressTokens: Map<String, String>? = null,
    @JsonNames("architectures")
    val architecture: Map<String, String>? = null,
    @JsonNames("finetuning_task")
    val fineTuningTask: String? = null,
    @JsonNames("id2label")
    var id2Label: Map<Int, String>? = null,
    @JsonNames("label2id")
    val label2Id: Map<String, Int> = emptyMap(),
    @JsonNames("num_labels")
    var numberOfLabels: Int? = null,
    @JsonNames("tokenizer_class")
    val tokenizerClass: String? = null,
    @JsonNames("prefix")
    val prefix: String? = null,
    @JsonNames("bos_token_id")
    val bosTokenId: Long? = null,
    @JsonNames("eos_token_id")
    val eosTokenId: Long? = null,
    @JsonNames("pad_token_id")
    val padTokenId: Long? = null,
    @JsonNames("sep_token_id")
    val sepTokenId: Long? = null,
    @JsonNames("decoder_start_token_id")
    val decoderStartTokenId: Long? = null,
    @JsonNames("task_specific_params")
    val taskSpecificParameters: Map<String, String> = emptyMap(),
    @JsonNames("problem_type")
    val problemType: ProblemType? = null,
    @JsonNames("name_or_path")
    val nameOrPath: String = "",
    @JsonNames("commit_hash")
    val commitHash: String? = null,
    @JsonNames("xla_device")
    val xlaDevice: String? = null,
    @JsonNames("gradient_checkpointing")
    val gradientCheckpointing: Boolean = false,
    var token: String? = null,
    @JsonNames("use_auth_token")
    val authToken: String? = null
)