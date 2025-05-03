FROM openjdk:25-oraclelinux9

RUN microdnf -y install \
    libX11 \
    libXext \
    libXrender \
    libXtst \
    libXi \
    libXrandr \
    xauth \
    && microdnf clean all

WORKDIR /app

CMD ["bash"]
