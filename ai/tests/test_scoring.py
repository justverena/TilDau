from scroing.overall_score import compute_overall_score
audio_path = "tests/sample.wav"
expected_text = "Жазға жетсең, қыс күніңді ұмытпа"

result = compute_overall_score(audio_path, expected_text)

print(result)