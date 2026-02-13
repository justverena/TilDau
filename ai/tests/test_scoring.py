from scroing.overall_score import compute_overall_score
path = "/sample.wav"
expected_text = "Жазға жетсең, қыс күніңді ұмытпа"
result = compute_overall_score(
            audio_path=path,
            expected_text=expected_text
        )

print(result)