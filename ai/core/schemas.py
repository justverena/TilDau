# core/schema.py
from pydantic import BaseModel
from typing import List, Dict, Any

class AnalyzeRequest(BaseModel):
    audio_url: str
    expected_text: str
    exercise_type: str = "READ_ALOUD"

class AnalyzeResponse(BaseModel):
    pronunciation_score: int
    fluency_score: int
    embedding_score: int
    overall_score: int
    flags: List[str]
    feedback: List[str]
    metrics: Dict[str, Any]