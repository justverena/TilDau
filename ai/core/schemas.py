from pydantic import BaseModel
from typing import List, Dict

class AnalyzeRequest(BaseModel):
    audio_url: str
    expected_text: str
    exercise_type: str

class AnalyzeResponse(BaseModel):
    pronunciation_score: float
    fluency_score: float
    overall_score: float
    meta: Dict[str, float | str]
    feedback: List[str]