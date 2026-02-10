from audio.utils import download_audio
import os

url = "http://localhost:9000/exercise-references/stuttering/u1/ex3.wav?AWSAccessKeyId=minioadmin&Signature=J13QJf54MVrtAlHsKkVjLrEdZvQ%3D&Expires=1770719756"
path = download_audio(url)
print("Downloaded to:", path)
print("Exists:", os.path.exists(path))
print("Size:", os.path.getsize(path))