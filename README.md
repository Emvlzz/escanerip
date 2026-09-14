* **Interfaz gráfica**: Se creó la ventana principal con Java Swing. Incluye las cajas de texto para ingresar la IP inicial, la IP final y el tiempo de espera, además de los botones de acción, una tabla de resultados y la barra de progreso.
* **Validación de entradas**: Se programó la comprobación de las direcciones IP con expresiones regulares para asegurar que el formato sea correcto antes de iniciar el escaneo.
* **Motor de ping multihilo**: Se implementó el escaneo en segundo plano utilizando hilos para procesar las conexiones sin congelar la ventana ni interrumpir la experiencia de usuario.
* **Tabla interactiva**: Se configuró la tabla para mostrar el estado y tiempo de respuesta de cada equipo, permitiendo ordenar las columnas al hacer clic sobre sus encabezados y contando los equipos activos.
  
