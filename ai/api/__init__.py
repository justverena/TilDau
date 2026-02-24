from fastapi import FastAPI
from .analyze import router as analyze_router

app = FastAPI(title="TilDau AI Module")
app.include_router(analyze_router, prefix="/ai")