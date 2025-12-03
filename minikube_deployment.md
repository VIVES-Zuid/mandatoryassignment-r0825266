### MongoDB Credentials
These credentials were used inside the Kubernetes Secret.  
The values in the `secret.yaml` file are Base64 encoded versions of the following:

MONGO_USER = warehouse_user_Andang  
MONGO_PASSWORD = Andang@79

Base64 encoding:
warehouse_user → d2FyZWhvdXNlX3VzZXJfQW5kYW5n
warehouse_pass → QW5kYW5nQDc5

Generated in CLI(powershell) user: 
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("warehouse_user_Andang"))
d2FyZWhvdXNlX3VzZXJfQW5kYW5n
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("Andang@79"))
QW5kYW5nQDc5