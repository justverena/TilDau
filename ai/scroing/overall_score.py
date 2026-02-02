import librosa

from fluency.fluency_metrics import compute_fluency_metrics
from asr.whisper_asr import transcribe_audio
from asr.asr_metrics import compute_asr_metrics
from scroing.fluency_score import score_fluency
from scroing.pronunciation_score import score_pronunciation

def compute_overall_score(audio_path: str, expected_text: str, sr=16000):
    """
    compute fluency, pronunciation and overall scores
    from audio + expected text.
    returns:
        {
            "fluency_score": int,
            "pronunciation_score": int,
            "overall_score": int,
            "flags": list[str],
            "metrics": {
                "fluency_metrics": {...},
                "asr_metrics": {...}
            }
        }
    """
    recognized_text = transcribe_audio(audio_path)

    asr_metrics = compute_asr_metrics(expected_text, recognized_text)
    pron_score_dict = score_pronunciation(asr_metrics)

    audio, _ = librosa.load(audio_path, sr=sr, mono=True)
    fluency_metrics = compute_fluency_metrics(audio, sr, expected_text)
    fluency_score_dict = score_fluency(fluency_metrics)

    overall_score = int((fluency_score_dict["fluency_score"] + pron_score_dict["pronunciation_score"]) / 2)

    flags = fluency_score_dict["flags"] + pron_score_dict["flags"]

    return {
        "fluency_score": fluency_score_dict["fluency_score"],
        "pronunciation_score": pron_score_dict["pronunciation_score"],
        "overall_score": overall_score,
        "flags": flags,
        "metrics": {
            "fluency_metrics": fluency_metrics,
            "asr_metrics": asr_metrics,
            "recognized_text": recognized_text
        }
    }