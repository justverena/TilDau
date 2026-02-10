import numpy as np

class ReferenceEmbeddingStore:
    def __init__(self, embeddings: list[np.ndarray]):
        self.embeddings = embeddings
        self.mean_embedding = np.mean(embeddings, axis=0)

    def get_reference(self):
        return self.mean_embedding

    @classmethod
    def from_file(cls, path: str):
        embedding = np.load(path)
        instance = cls([embedding])
        instance.mean_embedding = embedding
        return instance