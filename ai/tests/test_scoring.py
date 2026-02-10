from scroing.overall_score import compute_overall_score

result = compute_overall_score(
    audio_url="http://localhost:9000/exercise-references/stuttering/u1/ex3.wav?AWSAccessKeyId=minioadmin&Signature=J13QJf54MVrtAlHsKkVjLrEdZvQ%3D&Expires=1770719756",
    expected_text="Қызмет қыл да, міндет қыл"
)

print(result)