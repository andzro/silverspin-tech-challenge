# To learn more about how to use Nix to configure your environment
# see: https://developers.google.com/idx/guides/customize-idx-env
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-24.05"; # or "unstable"
  services.docker.enable = true;

  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.apt
    pkgs.jdk21
    pkgs.gradle
    pkgs.docker-compose
    pkgs.php81Packages.composer
    pkgs.php
    pkgs.netcat-gnu
  ];

  # Sets environment variables in the workspace
  env = {};

  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "redhat.java"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-dependency"
      "vscjava.vscode-java-pack"
      "vscjava.vscode-java-test"
      "vscjava.vscode-maven"
      "Pivotal.vscode-boot-dev-pack"
      "vmware.vscode-spring-boot"
      "vscjava.vscode-spring-boot-dashboard"
      "vscjava.vscode-spring-initializr"
      "java-extension-pack-jdk"
    ];

    # Enable previews
    previews = {
      enable = true;
    };

    # Workspace lifecycle hooks
    workspace = {
      # Runs when a workspace is first created
      onCreate = {
        # Ensure dependencies are installed if needed
        install-deps = "composer install --working-dir=frontend-system";
      };
      # Runs when the workspace is (re)started
      onStart = {
        # Start databases and Kafka
        start-databases = "docker-compose -f docker-compose-db.yml up -d";
        start-kafka = "docker-compose -f docker-compose-kafka.yml up -d";

        # Wait for PostgreSQL, MySQL, and Kafka to be ready
        wait-for-services = ''
          echo "Waiting for PostgreSQL (5432)..."
          while ! nc -z 127.0.0.1 5432; do sleep 1; done
          echo "PostgreSQL is up!"

          echo "Waiting for MySQL (3306)..."
          while ! nc -z 127.0.0.1 3306; do sleep 1; done
          echo "MySQL is up!"

          echo "Waiting for Kafka (9092)..."
          while ! nc -z 127.0.0.1 9092; do sleep 1; done
          echo "Kafka is up!"
        '';

        # Start backend services after services are ready
        start-ordering-system = "cd ordering-system && ./gradlew bootRun &";
        start-shipping-system = "cd shipping-system && ./gradlew bootRun &";

        # Start Laravel frontend system
        start-frontend-system-migration = "cd frontend-system && php artisan migrate";
        start-frontend-system = "cd frontend-system && php artisan serve --port=8000 &";
      };
    };
  };
}
