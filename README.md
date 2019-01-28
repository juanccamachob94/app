# Cálculo de bonificación para usuarios de PlayBusiness
## Descripción:
Prueba técnica. Desarrollo de una aplicación que calcula la bonificación para los usuarios del sistema con base en reglas dinámicas.
## Tecnologías:
* Ruby 2.5.3p105
* Rails 5.2.2
* Postgres 10.6
* Gems:
    * Devise 5.1
    * simplecov 0.16.0
    * rspec-rails 3.8
    * Factory_bot_rails 5.0.0.r2 (opcional)
## Ejecución
La aplicación se ejecuta mediante el comando
```
rails server
```
## Servicio:
El servicio `bonus_calculation` resuelve el requerimiento recibiendo el `ID` del usuario y devuelve un hash con los valores `lower_limit`, `upper_limit` y `bonus`. Adicionalmente se propone una alternativa al requerimiento mediante una función en el motor de la base de datos que permita solucionar la necesidad minimizando la carga de datos a través de la red. A continuación se describe la lógica de este algoritmo, el cual se aplica tanto para el servicio como para la propuesta en el motor de base de datos.
1. Se calcula el número de inversiones válidas para el usuario. Si el número de inversiones válidas es de 0, no existe bonificación.
2. Se calcula el promedio de inversión y la desviación estándar.
3. Se obtiene el intervalo al que corresponde el coeficiente de variación (obtenido de la división entre la desviación estándar y el promedio de inversión) y el intervalo al que corresponde el promedio de inversión
4. Si no existe un intervalo para el coeficiente de variación (por ejemplo cuando el coeficiente de variación es menor a 0.14 o la  desviación estándar es 0) se reasigna la desviación estándar con los valores predefinidos en la tabla de una sola inversión ([primera tabla](https://docs.google.com/spreadsheets/d/1xGovhmmAhFAbkWAhlaULOZk5QNJoEceSsS4BAal_S2U/edit)) y se reasigna el intervalo del coeficiente de variación a [0.3,1.3)
5. Se obtiene los valores para la bonificación a partir de el intervalo de inversiones y el intervalo de coeficientes de variación.
6. Se calcula la bonificación a partir de los valores de bonificación y la desviación estándar

### bonus_calculation:
La lógica desarrollada en este servicio se resuelve principalmente mediante el uso de scopes. El resultado se construye operando los valores mediante un método adicional en el mismo controlador (`ApplicationController`)
#### Scopes:
##### Investment:
* `valid_user_investments`: inversiones donde la diferencia entre amount y wallet_amount sean mayores a 3000.
* `valid_investments`: inversiones donde la diferencia entre amount y wallet_amount sean mayores a 3000 para el usuario que tiene por id el valor recibido como parámetro.
* `amount_difference_summary`: sumatoria de la diferencia de amount y wallet_amount de la tabla de inversiones.
* `amount_pow_difference_summary`: sumatoria de la diferencia de amount, wallet_amount y el promedio de inversiones recibido como parámetro, elevados al cuadrado.
#### InvestmentInterval:
* `get_intervals`: obtiene los intervalos de inversión que contengan en un conjunto cerrado al valor recibido como parámetro o cuyo valor sea menor al valor máximo o mayor al valor mínimo.
#### CvInteral:
* `get_intervals`: obtiene los intervalos de coeficientes de variación que contengan en un conjunto cerrado por izquierda y abierto por derecha, (x > min y x <= max) al valor recibido como parámetro o cuyo valor sea menor al valor máximo o mayor al valor mínimo.

### bonus_calculation_alternative:
En el controlador (`ApplicationController`) se utiliza la función `bonus_calculation` desarrollada en postgres. Se recuerda que esta alternativa se construye con el propósito de minimizar costos de ejecución.

## Modelo de datos
El modelo relacional que describe la solución al problema se puede observar en este [enlace](https://drive.google.com/file/d/1hKTG_8SAtIJOQH_FTPUcIFsgNN2c1ulm/preview). A continuación se describe cada una de las tablas, sus atributos y relaciones.
#### USERS:
- **Descripción:** usuarios de la aplicación.
- **Atributos:**
  - ID: llave primaria auto-incremental.
  - EMAIL: correo electrónico del usuario. Únicamente tiene carácter representativo.
#### INVESTMENTS:
- **Descripción:** inversiones realizadas por los usuarios de la aplicación.
- **Atributos:**
  - ID: llave primaria auto-incremental.
  - AMOUNT: monto de inversión total que realiza un usuario a un startup.
  - WALLET_AMOUNT: monto de inversión virtual que realiza un usuario aun startup.
  - USER_ID: Identificador del usuario al que pertenece dicha inversión.
#### INVESTMENT_INTERVALS:
- **Descripción:** Intervalo de inversiones en el que se agrupa el promedio de inversiones de un usuario, de acuerdo al plan que corresponde. Cada plan indica agrupa los intervalos de inversiones con valores disyuntos entre ellos.
- **Atributos:**
  - ID: llave primaria auto-incremental
  - MIN: valor mínimo del intervalo al que puede pertenecer el promedio de inversiones de un usuario.
  - MAX: valor máximo del intervalo al que puede pertenecer el promedio de inversiones de un usuario.
  - COLLECTION: identificador de agrupamiento que identifica a un intervalo de inversiones dentro de un plan.
#### CV_INTERVALS:
- **Descripción:** Intervalo de coeficientes de variación en el que se agrupa el CV (coeficiente de variación) calculado de las inversiones de un usuario. Cada intervalo está definido por un conjunto cerrado en el valor mínimo y abierto en el valor máximo ([min,max)).
- **Atributos:**
  - ID: llave primaria auto-incremental
  - MIN: valor mínimo del intervalo al que puede pertenecer el CV calculado para el usuario.
  - MAX: valor máximo del intervalo al que puede pertenecer el CV calculado para el usuario.
#### CV_INVESTMENT_INTERVALS:
- **Descripción:** cruce de información entre las tablas CV_INTERVALS e INVESTMENT_INTERVALS con la finalidad de agrupar los intervalos de inversiones que pertenecen a un intervalo de CV (coeficiente de variación).
- **Atributos:**
  - ID: llave primaria auto-incremental.
  - CV_INTERVAL_ID: llave que referencia el ID de la tabla CV_INTERVALS.
  - INVESTMENT_INTERVAL_ID: llave que referencia el ID de la tabla INVESTMENT_INTERVALS.
#### BONUS:
- **Descripción:** valores base para el cálculo de la bonificación de los usuarios.
- **Atributos:**
  - ID: llave primaria auto-incremental
  - STD: desviación estándar
  - PERCENT: porcentaje de bonificación
  - CV_INVESTMENT_INTERVAL_ID: llave que referencia el ID de la tabla CV_INVESTMENT_INTERVALS para indicar a qué agrupación de intervalos de inversiones y coeficientes de variación pertenece la bonificación.
#### UNIT_BONUS:
- **Descripción:** valores definidos para una sola inversión válida o para coeficientes de variación menores a 0.14 (CV < 0.14)
- **Atributos:**
  - STDV: desviación estándar poblacional
  - INVESTMENT_INTERVAL_ID: llave que referencia el ID de la tabla INVESTMENT_INTERVALS para indicar el intervalo de inversiones al que pertenece la desviación estándar.
