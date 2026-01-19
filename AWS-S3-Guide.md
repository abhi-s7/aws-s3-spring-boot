# AWS S3 Setup Guide

This document outlines the end-to-end process for setting up AWS S3 access for the **AWS-S3-Spring-Boot** application.

---

## What is Amazon S3?

**Amazon S3 (Simple Storage Service)** is AWS's object storage service for storing and retrieving unlimited data with 99.999999999% durability. Common uses include backups, static website hosting, application data storage, and content distribution.

---

## 1. Create an IAM User (Identity)

We create a dedicated IAM user with programmatic access instead of using root credentials for security best practices.

**Steps:**
1.  Go to **IAM** → **Users** → **Create user**.
2.  **User name**: `s3-spring-boot-user`
3.  **Access type**: Enable **Programmatic access** (this generates Access Keys).
4.  Click **Next: Permissions**.

---

## 2. Attach S3 Permissions Policy

The user needs permission to perform S3 operations (create buckets, upload/download/delete files).

### Option A: Full S3 Access (Development/Testing)
1.  Select **Attach existing policies directly**.
2.  Search for `AmazonS3FullAccess`.
3.  Check the box and click **Next: Tags** → **Next: Review** → **Create user**.

### Option B: Custom Minimal Policy (Production - Recommended)
If you want tighter security, create a custom policy with only required permissions:

**Steps:**
1.  Go to **IAM** → **Policies** → **Create policy**.
2.  Click **JSON** tab and paste the following policy:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "Statement1",
			"Principal": "*",
            "Effect": "Allow",
            "Action": [
                "s3:CreateBucket",
                "s3:ListBucket",
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject"
            ],
            "Resource": [
                "arn:aws:s3:::*",
                "arn:aws:s3:::*/*"
            ]
        }
    ]
}
```

3.  Click **Next: Tags** → **Next: Review**.
4.  **Policy name**: `S3-Spring-Boot-Custom-Policy`
5.  Click **Create policy**.
6.  Go back to your user → **Add permissions** → **Attach policies directly** → Select your custom policy.

---

## 3. Generate Access Keys

After creating the user, you need to generate access keys for programmatic access. AWS will show you the **Access Key ID** and **Secret Access Key** ONCE. You must save these immediately.

**Steps:**
1.  Go to **IAM** → **Users** → Select your user (`s3-spring-boot-user`).
2.  Click **Security credentials** tab.
3.  Scroll to **Access keys** section → Click **Create access key**.
4.  Select **Use case**: Choose **Application running outside AWS**.
5.  Click **Next** → Add description (optional) → Click **Create access key**.
6.  AWS displays:
    *   **Access key ID**: `AKIAIOSFODNN7EXAMPLE`
    *   **Secret access key**: `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY`
7.  Click **Download .csv file** to save these credentials.
8.  **Important**: Keep these secure. Never commit them to version control.

**If you lose the keys:**
*   You cannot retrieve the secret key again.
*   You must create a new access key pair.
*   Delete old keys if no longer needed for security.

---

## 4. Create an S3 Bucket (Manual Setup)

You can create buckets manually via the AWS Console or let the application create them programmatically. This guide covers manual creation for learning purposes.

**Steps:**
1.  Go to **S3** → **Buckets** → **Create bucket**.

2.  **Bucket name**: Must be globally unique (e.g., `abhi-s3-spring-boot-demo-2026`).
    *   **Why unique?** S3 bucket names are shared across all AWS accounts worldwide.
    *   **Rules**: Lowercase letters, numbers, hyphens only. No underscores or spaces.

3.  **AWS Region**: Select closest to your location (e.g., `us-west-1`).
    *   **Important**: Remember this region - you'll need it in your `.env` file.

4.  **Object Ownership**: Keep default (ACLs disabled).

5.  **Block Public Access settings**:
    *   **Keep all 4 checkboxes enabled** (default).
    *   **Why?** This application uses IAM credentials and pre-signed URLs, not public access.
    *   *Only disable if you specifically need public file access (see Step 5 below).*

6.  **Bucket Versioning**: Optional (disable for simplicity).
    *   **What it does**: Keeps multiple versions of files when you overwrite them.

7.  **Tags**: Optional (helps organize resources for billing/management).

8.  **Default encryption**: Keep default (Server-side encryption with Amazon S3 managed keys - SSE-S3).
    *   **Why?** Free encryption at rest for your files.

9.  Click **Create bucket**.

**Result**: You now have a private S3 bucket ready for use.

---

## 5. Bucket Policy (Optional - For Public Access)

**Skip this step if you only need private access (default for this application).**

**Scenario**: If you want files in your bucket to be publicly readable (like hosting images for a website), you need a bucket policy.

**Warning**: This makes ALL objects in the bucket public. Use with caution.

### Steps to Enable Public Access:

1.  Go to **S3** → Select your bucket → **Permissions** tab.

2.  **Block public access (bucket settings)**:
    *   Click **Edit**.
    *   Uncheck **"Block all public access"**.
    *   Click **Save changes**.
    *   Type `confirm` in the confirmation box.

3.  **Bucket policy**:
    *   Scroll down to **Bucket policy** section.
    *   Click **Edit**.
    *   Paste this policy:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::YOUR-BUCKET-NAME/*"
        }
    ]
}
```

4.  **Replace `YOUR-BUCKET-NAME`** with your actual bucket name (e.g., `abhi-s3-spring-boot-demo-2026`).

5.  Click **Save changes**.

**Result**: Any file you upload will be accessible via:
```
https://YOUR-BUCKET-NAME.s3.REGION.amazonaws.com/filename.jpg
```

**Example**:
```
https://abhi-s3-spring-boot-demo-2026.s3.us-west-1.amazonaws.com/profile.jpg
```

---

## 6. Configure Application Environment Variables

Now that you have your AWS credentials and bucket, configure the application to use them.

**Steps:**

1.  **Navigate to project directory**:
    ```bash
    cd aws-s3-spring-boot
    ```

2.  **Copy the example file**:
    ```bash
    cp .env.example .env
    ```

3.  **Edit the `.env` file**:
    ```bash
    # On Mac/Linux
    nano .env
    
    # On Windows
    notepad .env
    ```

4.  **Fill in your AWS credentials**:
    ```env
    AWS_S3_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE
    AWS_S3_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
    AWS_S3_REGION=us-west-1
    ```

    *Replace the example values with your actual credentials from Step 3.*

5.  **Save and close** the file.

**Security Note:**
*   The `.env` file is already added to `.gitignore` to prevent accidental commits.
*   Never share your Secret Access Key publicly or commit it to version control.
*   If you accidentally expose your keys, delete them immediately in IAM and create new ones.

---

## 7. Verify Configuration

Ensure your IAM user has S3 permissions, access keys are saved in `.env`, and the region matches your bucket. Test by running the application and creating a test bucket via the UI.

---

## 8. Troubleshooting

### Error: "Access Denied"
*   **Cause**: Invalid credentials or insufficient IAM permissions.
*   **Fix**: 
    1.  Verify your Access Key and Secret Key in `.env`.
    2.  Check IAM user has S3 permissions attached.
    3.  Ensure no typos in credentials (no extra spaces).

### Error: "Bucket already exists"
*   **Cause**: S3 bucket names are globally unique across all AWS accounts.
*   **Fix**: Choose a different bucket name (try adding numbers or your initials).

### Error: "The bucket you are attempting to access must be addressed using the specified endpoint"
*   **Cause**: Region mismatch - bucket exists in different region than specified.
*   **Fix**: Update `AWS_S3_REGION` in `.env` to match your bucket's region.

### Error: "The security token included in the request is invalid"
*   **Cause**: Access key has been deleted or deactivated in IAM.
*   **Fix**: Create new access keys in IAM console and update `.env`.

### Error: "SignatureDoesNotMatch"
*   **Cause**: Secret key is incorrect or contains extra characters.
*   **Fix**: Re-download credentials CSV and carefully copy the secret key (watch for trailing spaces).

---

## 9. Best Practices

*   **Never hardcode credentials**: Use environment variables or IAM roles.
*   **Use IAM roles on EC2**: Attach an IAM role instead of access keys when deploying to EC2.
*   **Enable MFA**: Add Multi-Factor Authentication for extra security.
*   **Enable versioning**: Protects against accidental deletions.
*   **Set up replication and backups**: Copy data to another region for disaster recovery.
*   **Choose correct region**: Select closest to your users for better performance.
