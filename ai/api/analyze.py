from fastapi import APIRouter, UploadFile, File, Form
import tempfile
import shutil
import os
import logging
from core.schemas import AnalyzeResponse
from scroing.overall_score import compute_overall_score
from feedback.rules import generate_feedback

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

router = APIRouter()

@router.post("/analyze", response_model=AnalyzeResponse)
async def analyze(
    file: UploadFile = File(...),
    expectedText: str = Form(...)
):
    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
        shutil.copyfileobj(file.file, tmp)
        tmp_path = tmp.name

    result = compute_overall_score(
        audio_path=tmp_path,
        expected_text=expectedText
    )
    
    logger.info(f"ASR recognized text: {result['metrics'].get('recognized_text')}")
    logger.info(f"Scores: {result}")

    result["feedback"] = generate_feedback(result["flags"])

    return result