import librosa
from preprocessing.features import compute_basic_features

def runtime_preprocess(audio_path: str, sr=16000):
    audio, _ = librosa.load(audio_path, sr=sr, mono=True)
    features = compute_basic_features(
        audio=audio,
        trimmed_audio=audio,
        sr=sr
    )

    return audio, features