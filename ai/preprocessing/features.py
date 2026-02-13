import librosa
import numpy as np


def trim_silence(audio, sr, top_db=25):
    trimmed, _ = librosa.effects.trim(audio, top_db=top_db)
    return trimmed


def compute_basic_features(audio, trimmed_audio, sr):
    duration = len(audio) / sr
    voiced_duration = len(trimmed_audio) / sr

    return {
        "duration": round(duration, 3),
        "voiced_duration": round(voiced_duration, 3)
    }