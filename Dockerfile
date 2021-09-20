# App Building phase --------
FROM gradle:jdk11 as builder

COPY --chown=gradle:gradle /backend /home/gradle/src

WORKDIR /home/gradle/src
RUN gradle build
RUN tar -C /usr/local/bin -xf build/distributions/Bantamweight.tar

ARG litestream_version="0.3.5"
ARG litestream_tar_filename="litestream-v${litestream_version}-linux-amd64-static.tar.gz"
# Download the static build of Litestream directly into the path & make it executable.
# This is done in the builder and copied as the chmod doubles the size.
ADD https://github.com/benbjohnson/litestream/releases/download/v${litestream_version}/${litestream_tar_filename} /tmp/litestream.tar.gz
RUN tar -C /usr/local/bin -xzf /tmp/litestream.tar.gz
# End App Building phase --------

# Container setup --------
FROM openjdk:11-jre-slim

# Creating user
RUN adduser --group --system appuser

# Copy executables from builder
COPY --from=builder /usr/local/bin/Bantamweight /usr/local/bin/Bantamweight
COPY --from=builder /usr/local/bin/litestream /usr/local/bin/litestream

# Create data directory (although this will likely be mounted too)
RUN mkdir -p /data
RUN chown appuser:appuser /data

COPY /secrets/google-creds.json* /etc/google-creds.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/etc/google-creds.json
ENV REPLICA_URL=gcs://bantamweight

# Copy Litestream configuration file & startup script.
COPY etc/litestream.yml /etc/litestream.yml
COPY scripts/run.sh /scripts/run.sh
RUN chown appuser:appuser /scripts/run.sh
RUN chmod 766 /scripts/run.sh

# container exposes if not set - overridden by Heroku
EXPOSE 9000

# Setting user to use when running the image 
USER appuser

CMD ["/scripts/run.sh"]
# End Container setup --------