from fastapi import APIRouter, UploadFile, File, Form
import tempfile
import shutil
import os

from core.schemas import AnalyzeResponse
from scroing.overall_score import compute_overall_score
from feedback.rules import generate_feedback

router = APIRouter()

@router.post("/analyze", response_model=AnalyzeResponse)
async def analyze(
    audio: UploadFile = File(...),
    expected_text: str = Form(...)
):
    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
        shutil.copyfileobj(audio.file, tmp)
        tmp_path = tmp.name

    result = compute_overall_score(
        audio_path=tmp_path,
        expected_text=expected_text
    )

    result["feedback"] = generate_feedback(result["flags"])

    return result