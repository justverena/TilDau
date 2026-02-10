import requests
import tempfile

def download_audio(audio_url: str) -> str:
    response = requests.get(audio_url)
    response.raise_for_status()

    tmp = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
    tmp.write(response.content)
    tmp.close()

    return tmp.name