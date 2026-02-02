from fastapi import APIRouter
from pydantic import BaseModel
from scroing.overall_score import compute_overall_score
from feedback.rules import generate_feedback

router = APIRouter()

class AnalyzeRequest(BaseModel):
    audio_path: str
    expected_text: str
    exercise_type: str = "READ_ALOUD"

@router.post("/analyze")
def analyze(req: AnalyzeRequest):
    result = compute_overall_score(req.audio_path, req.expected_text)
    result["feedback"] = generate_feedback(result["flags"])
    return result