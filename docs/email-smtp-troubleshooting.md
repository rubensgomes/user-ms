# Email / SMTP Troubleshooting

## Symptom

`EmailService.sendConfirmationEmail` (and `sendPasswordResetEmail`) fail at
runtime with:

```
org.springframework.mail.MailSendException: Mail server connection failed.
  Caused by: org.eclipse.angus.mail.util.MailConnectException:
    Couldn't connect to host, port: smtp.gmail.com, 587; timeout -1;
  Caused by: java.net.ConnectException: Connection timed out
```

The failure happens inside Spring's `JavaMailSenderImpl.connectTransport`
during the TCP connect — **before** any SMTP/auth handshake. This means the
JVM never reached Gmail's SMTP server, so the problem is network reachability,
not credentials.

## Likely causes (most → least common)

1. **Outbound port 587 is blocked.** Corporate networks, ISPs, and VPNs
   commonly block SMTP submission ports. Verify from the same shell that runs
   the app:
   ```bash
   nc -vz smtp.gmail.com 587
   # or
   curl -v telnet://smtp.gmail.com:587
   ```
   If the probe hangs or fails, the JVM will too. Fix at the network layer
   (different network, disable VPN) or switch to port 465 (SMTPS).

2. **WSL2 networking quirk.** If Windows can reach the host but WSL2 cannot,
   run from Windows PowerShell:
   ```powershell
   Test-NetConnection smtp.gmail.com -Port 587
   ```
   If Windows works and WSL doesn't, restart WSL:
   ```
   wsl --shutdown
   ```

3. **Env vars not set or using placeholders.** `application.yml` defaults to
   `your-email@gmail.com` / `your-app-password`. Confirm `MAIL_USERNAME` and
   `MAIL_PASSWORD` are exported in the process environment. Gmail requires a
   **16-character App Password** (2FA must be enabled); a normal account
   password will not authenticate even once the network is reachable.

## Recommended code/config improvements

- **Fail fast on connect.** Add the following under
  `spring.mail.properties.mail.smtp` in `application.yml` so the client does
  not hang on `timeout -1`:
  ```yaml
  connectiontimeout: 5000
  timeout: 5000
  writetimeout: 5000
  ```

- **Port 465 fallback (SMTPS).** If port 587 is permanently blocked on the
  deployment network:
  ```yaml
  spring:
    mail:
      port: 465
      properties:
        mail:
          smtp:
            ssl:
              enable: true
            starttls:
              enable: false
  ```

- **Downgrade log level for non-critical failures.** `EmailService` currently
  logs full stack traces at ERROR and rethrows `MailSendException` from an
  `@Async` method, which surfaces a second stack trace via
  `SimpleAsyncUncaughtExceptionHandler`. If email is not critical to the
  registration flow, consider logging at WARN and swallowing the exception
  (or routing to a retry queue).

## Relevant files

- `app/src/main/java/com/rubensgomes/userms/service/EmailService.java`
- `app/src/main/resources/application.yml` (mail config, lines 23-34)
