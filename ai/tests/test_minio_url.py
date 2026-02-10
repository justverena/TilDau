import boto3

s3 = boto3.client(
    "s3",
    endpoint_url="http://localhost:9000",
    aws_access_key_id="minioadmin",
    aws_secret_access_key="minioadmin",
    region_name="us-east-1"
)

presigned_url = s3.generate_presigned_url(
    ClientMethod='get_object',
    Params={'Bucket': 'exercise-references', 'Key': 'stuttering/u1/ex3.wav'},
    ExpiresIn=3600
)
print(presigned_url)