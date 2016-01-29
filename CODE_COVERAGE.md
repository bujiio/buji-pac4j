# Code Coverage Report generation

To generate the code coverage report, execute the following command:
> mvn clean package

This will generate code coverage report in each of the modules. In order to view the same, open the following file in your browser.
> ROOT/MODULE_NAME/target/site/cobertura/index.html

Please note that the above folder is created under each of the modules. For example:
> bujiio-buji-pac4j/core/target/site/cobertura/index.html
> bujiio-buji-pac4j/servlet/target/site/cobertura/index.html