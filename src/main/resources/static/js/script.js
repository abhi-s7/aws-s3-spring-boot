async function createBucket(event) {
    event.preventDefault();
    const bucketName = document.getElementById('bucketName').value;
    const resultDiv = document.getElementById('createBucketResult');

    try {
        const response = await axios.post(`/api/s3/create-bucket/${bucketName}`);
        resultDiv.innerText = response.data;
        resultDiv.className = 'result';
    } catch (error) {
        let errorMsg = "Bucket creation failed: ";
        if (error.response && error.response.data) {
            errorMsg += error.response.data;
        } else if (error.message) {
            errorMsg += error.message;
        } else {
            errorMsg += "Unknown error";
        }
        resultDiv.innerText = errorMsg;
        resultDiv.className = 'result error';
    }
}

async function uploadFile(event) {
    event.preventDefault();
    const bucketName = document.getElementById('uploadBucketName').value;
    const file = document.getElementById('file').files[0];
    const resultDiv = document.getElementById('uploadFileResult');

    const formData = new FormData();
    formData.append('file', file);
    formData.append('bucketName', bucketName);

    try {
        const response = await axios.post('/api/s3/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        resultDiv.innerText = response.data;
        resultDiv.className = 'result';
    } catch (error) {
        let errorMsg = "File upload failed: ";
        if (error.response && error.response.data) {
            errorMsg += error.response.data;
        } else if (error.message) {
            errorMsg += error.message;
        } else {
            errorMsg += "Unknown error";
        }
        resultDiv.innerText = errorMsg;
        resultDiv.className = 'result error';
    }
}

async function downloadFile(event) {
    event.preventDefault();
    const bucketName = document.getElementById('downloadBucketName').value;
    const fileName = document.getElementById('downloadFileName').value;
    const resultDiv = document.getElementById('downloadFileResult');

    try {
        const response = await axios.get(`/api/s3/download/${bucketName}/${fileName}`, {
            responseType: 'blob'
        });

        const url = window.URL.createObjectURL(response.data);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        a.remove();

        window.URL.revokeObjectURL(url);
        resultDiv.innerText = "Download started.";
        resultDiv.className = 'result';
    } catch (error) {
        let errorMsg = "File download failed: ";
        
        if (error.response && error.response.data) {
            try {
                if (error.response.data instanceof Blob) {
                    const text = await error.response.data.text();
                    errorMsg += text;
                } else {
                    errorMsg += error.response.data;
                }
            } catch (blobError) {
                errorMsg += error.message || "Unknown error";
            }
        } else if (error.message) {
            errorMsg += error.message;
        } else {
            errorMsg += "Unknown error";
        }
        
        resultDiv.innerText = errorMsg;
        resultDiv.className = 'result error';
    }
}

async function deleteFile(event) {
    event.preventDefault();
    const bucketName = document.getElementById('deleteBucketName').value;
    const fileName = document.getElementById('deleteFileName').value;
    const resultDiv = document.getElementById('deleteFileResult');

    try {
        const response = await axios.delete(`/api/s3/delete/${bucketName}/${fileName}`);
        resultDiv.innerText = response.data;
        resultDiv.className = 'result';
    } catch (error) {
        let errorMsg = "File deletion failed: ";
        if (error.response && error.response.data) {
            errorMsg += error.response.data;
        } else if (error.message) {
            errorMsg += error.message;
        } else {
            errorMsg += "Unknown error";
        }
        resultDiv.innerText = errorMsg;
        resultDiv.className = 'result error';
    }
}
