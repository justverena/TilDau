import librosa
from fluency.fluency_metrics import compute_fluency_metrics
from fluency.tempo_utils import detect_pauses, compute_tempo
from scroing.fluency_score import score_fluency

audio, sr = librosa.load("tests/sample.wav", sr=16000)
text = "Жазға жетсең, қыс күніңді ұмытпа"

metrics = compute_fluency_metrics(audio, sr, text)
score = score_fluency(metrics)

print(metrics)
print(score)