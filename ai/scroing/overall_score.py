import librosa
import numpy as np
import os
from embedding.extractor import extract_embedding
from embedding.similarity import cosine_similarity
from fluency.fluency_metrics import compute_fluency_metrics
from asr.whisper_asr import transcribe_audio
from asr.asr_metrics import compute_asr_metrics
from scroing.fluency_score import score_fluency
from scroing.pronunciation_score import score_pronunciation
from scroing.embedding_score import score_embedding
from preprocessing.runtime import runtime_preprocess
from audio.utils import download_audio
base_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.abspath(os.path.join(base_dir, "../../"))
reference_embedding_path = os.path.join(project_root, "data/reference_embeddings/wav2vec2_reference_mean.npy")
reference_embedding = np.load(reference_embedding_path)

def compute_overall_score(audio_url: str, expected_text: str, sr=16000):
    audio_path = download_audio(audio_url)
    try:
        audio, basic_features = runtime_preprocess(audio_path, sr)

        recognized_text = transcribe_audio(audio_path)

        asr_metrics = compute_asr_metrics(expected_text, recognized_text)
        pron_score_dict = score_pronunciation(asr_metrics)

        fluency_metrics = compute_fluency_metrics(audio, sr, expected_text)
        fluency_metrics.update(basic_features)
        fluency_score_dict = score_fluency(fluency_metrics)

        user_embedding = extract_embedding(audio, sr)
        sim_score = cosine_similarity(user_embedding, reference_embedding)
        embedding_score_dict = score_embedding(sim_score)

        overall_score = int(
            (fluency_score_dict["fluency_score"] +
            pron_score_dict["pronunciation_score"] +
            embedding_score_dict["embedding_score"]) / 3
        )

        flags = fluency_score_dict["flags"] + pron_score_dict["flags"] + embedding_score_dict["flags"]

        return {
            "fluency_score": fluency_score_dict["fluency_score"],
            "pronunciation_score": pron_score_dict["pronunciation_score"],
            "embedding_score": embedding_score_dict["embedding_score"],
            "overall_score": overall_score,
            "flags": flags,
            "metrics": {
                "fluency_metrics": fluency_metrics,
                "asr_metrics": asr_metrics,
                "recognized_text": recognized_text,
                "embedding_similarity": sim_score
            },
        }
    finally:
        if os.path.exists(audio_path):
            os.remove(audio_path)