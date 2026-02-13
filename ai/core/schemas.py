from pydantic import BaseModel
from typing import List, Dict, Any

class AnalyzeResponse(BaseModel):
    pronunciation_score: int
    fluency_score: int
    embedding_score: int
    overall_score: int
    flags: List[str]
    feedback: List[str]
    metrics: Dict[str, Any]