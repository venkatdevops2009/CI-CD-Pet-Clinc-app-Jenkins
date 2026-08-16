#!/bin/bash

growpart /dev/nvme0n1 4
lvextend -L +10G /dev/mapper/RootVG-rootVol
lvextend -L +10G /dev/mapper/RootVG-homeVol
lvextend -L +10G /dev/mapper/RootVG-varVol

xfs_growfs /var
xfs_growfs /home
xfs_growfs /

# Java
yum install fontconfig java-21-openjdk -y

# NodeJS
dnf module enable nodejs:20 -y
dnf install nodejs -y

# Docker
dnf -y install dnf-plugins-core
dnf config-manager --add-repo https://download.docker.com/linux/rhel/docker-ce.repo
dnf install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y
systemctl start docker
systemctl enable docker
usermod -aG docker ec2-user

# eksctl
# for ARM systems, set ARCH to: `arm64`, `armv6` or `armv7`
ARCH=amd64
PLATFORM=$(uname -s)_$ARCH

curl -sLO "https://github.com/eksctl-io/eksctl/releases/latest/download/eksctl_$PLATFORM.tar.gz"
tar -xzf eksctl_$PLATFORM.tar.gz -C /tmp && rm eksctl_$PLATFORM.tar.gz
sudo install -m 0755 /tmp/eksctl /usr/local/bin && rm /tmp/eksctl

# kubectl
curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.35.3/2026-04-08/bin/linux/amd64/kubectl
chmod +x ./kubectl
cp kubectl /usr/local/bin/kubectl

# kubens (from kubectx project, pure bash version)
curl -sLo /tmp/kubens https://raw.githubusercontent.com/ahmetb/kubectx/master/kubens
install -m 0755 /tmp/kubens /usr/local/bin/kubens && rm /tmp/kubens

# k9s
curl -sLO https://github.com/derailed/k9s/releases/latest/download/k9s_Linux_amd64.tar.gz
tar -xzf k9s_Linux_amd64.tar.gz -C /tmp k9s && rm k9s_Linux_amd64.tar.gz
install -m 0755 /tmp/k9s /usr/local/bin/k9s && rm /tmp/k9s

#Helm
curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-4
chmod 700 get_helm.sh
./get_helm.sh

#Trivy
sudo tee /etc/yum.repos.d/trivy.repo <<EOF
[trivy]
name=Trivy repository
baseurl=https://aquasecurity.github.io/trivy-repo/rpm/releases/\$basearch/
gpgcheck=1
enabled=1
gpgkey=https://aquasecurity.github.io/trivy-repo/rpm/public.key
EOF

sudo dnf update -y
sudo dnf install trivy -y