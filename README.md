# Email CLI

A CLI tool to handle emails in an easy way

- [x] Get emails
- [x] Search email
- [x] Send email
- [x] Delete email

## How to build in local?

```bash
./mvnw clean package

java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-folders
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-emails INBOX --unread
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-emails INBOX --read
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-emails INBOX --from "sender@example.com"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-emails INBOX --subject "urgent"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-emails INBOX --unread --received-after "2025-12-01"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar list-emails INBOX --read --received-after "2025-12-01"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar delete-emails INBOX --unread
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar delete-emails INBOX --from "sender@example.com"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar delete-emails INBOX --subject "spam"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar delete-emails INBOX --received-before "2024-01-01"
java -jar ./cli/target/email-cli-0.1.0-SNAPSHOT.jar delete-emails INBOX --unread --from "sender@example.com"
```

## References

- https://datatracker.ietf.org/doc/html/rfc3501
- https://datatracker.ietf.org/doc/html/rfc1939
- https://datatracker.ietf.org/doc/html/rfc5321
- https://commons.apache.org/proper/commons-email/
- https://github.com/greenmail-mail-test/greenmail

