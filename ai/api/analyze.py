from fastapi import APIRouter
from core.schemas import AnalyzeRequest, AnalyzeResponse
from scroing.overall_score import compute_overall_score
from feedback.rules import generate_feedback

router = APIRouter()

@router.post("/analyze", response_model=AnalyzeResponse)
def analyze(req: AnalyzeRequest):
    result = compute_overall_score(
        audio_url=req.audio_url,
        expected_text=req.expected_text
    )

    result["feedback"] = generate_feedback(result["flags"])
    return result