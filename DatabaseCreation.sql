USE [db_h2oasis]
GO
/****** Object:  Table [dbo].[ajustesCuenta]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ajustesCuenta](
	[idAjuste] [int] IDENTITY(1,1) NOT NULL,
	[informacionPersonal] [varchar](50) NOT NULL,
	[informacionContacto] [varchar](50) NOT NULL,
	[idUsuario] [int] NOT NULL,
 CONSTRAINT [PK_ajustesCuenta] PRIMARY KEY CLUSTERED 
(
	[idAjuste] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[alertas]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[alertas](
	[idAlerta] [int] IDENTITY(1,1) NOT NULL,
	[mensaje] [varchar](50) NOT NULL,
	[fechaHora] [datetime] NOT NULL,
	[idCisterna] [int] NOT NULL,
 CONSTRAINT [PK_alertas] PRIMARY KEY CLUSTERED 
(
	[idAlerta] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[cisternaPlanta]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[cisternaPlanta](
	[idCisternaPlanta] [int] IDENTITY(1,1) NOT NULL,
	[idCisterna] [int] NOT NULL,
	[idPlanta] [int] NOT NULL,
 CONSTRAINT [PK_cisternaPlanta] PRIMARY KEY CLUSTERED 
(
	[idCisternaPlanta] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[cisternas]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[cisternas](
	[idCisterna] [int] IDENTITY(1,1) NOT NULL,
	[capacidad] [bigint] NOT NULL,
	[idAjuste] [int] NOT NULL,
	[nombreCorto] [varchar](50) NULL,
	[descripcion] [varchar](500) NULL,
 CONSTRAINT [PK_cisternas] PRIMARY KEY CLUSTERED 
(
	[idCisterna] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[configuracionesAlertas]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[configuracionesAlertas](
	[idConfiguracionAlerta] [int] IDENTITY(1,1) NOT NULL,
	[umbralNivelAgua] [float] NOT NULL,
	[idFrecuenciaAlertas] [int] NOT NULL,
	[idCisterna] [int] NOT NULL,
 CONSTRAINT [PK_configuracionesAlertas] PRIMARY KEY CLUSTERED 
(
	[idConfiguracionAlerta] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[frecuenciaAlertas]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[frecuenciaAlertas](
	[idFrecuenciaAlertas] [int] IDENTITY(1,1) NOT NULL,
	[tipo] [varchar](50) NOT NULL,
	[cantidad] [int] NOT NULL,
 CONSTRAINT [PK_frecuenciaAlertas] PRIMARY KEY CLUSTERED 
(
	[idFrecuenciaAlertas] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[historial]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[historial](
	[idHistorial] [int] IDENTITY(1,1) NOT NULL,
	[fechaHora] [datetime] NOT NULL,
	[nivelAgua] [float] NOT NULL,
	[cantidadAguaUtilizada] [float] NOT NULL,
	[idCisterna] [int] NOT NULL,
 CONSTRAINT [PK_historial] PRIMARY KEY CLUSTERED 
(
	[idHistorial] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[historialLog]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[historialLog](
	[idHistorialLog] [int] IDENTITY(1,1) NOT NULL,
	[tipo] [varchar](50) NOT NULL,
	[idUsuario] [int] NOT NULL,
	[fechaHora] [datetime] NOT NULL,
	[direccionIP] [varchar](20) NULL,
	[descripcion] [varchar](100) NULL,
 CONSTRAINT [PK_historialLog] PRIMARY KEY CLUSTERED 
(
	[idHistorialLog] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[mediciones]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[mediciones](
	[idMedicion] [int] IDENTITY(1,1) NOT NULL,
	[nivelAgua] [float] NOT NULL,
	[cantidadAguaUtilizada] [float] NOT NULL,
	[fechaHora] [datetime] NOT NULL,
	[idCisterna] [int] NULL,
 CONSTRAINT [PK_mediciones] PRIMARY KEY CLUSTERED 
(
	[idMedicion] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[movimientoCisterna]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[movimientoCisterna](
	[idMovimientoCisterna] [int] IDENTITY(1,1) NOT NULL,
	[tipoMovimiento] [varchar](50) NOT NULL,
	[descripcion] [varchar](50) NOT NULL,
	[descripcionDetalle] [varchar](250) NULL,
	[fechaHora] [datetime] NOT NULL,
	[idUsuario] [int] NOT NULL,
	[idCisterna] [int] NOT NULL,
 CONSTRAINT [PK_movimientoCisterna] PRIMARY KEY CLUSTERED 
(
	[idMovimientoCisterna] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[notificaciones]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[notificaciones](
	[idNotificacion] [int] IDENTITY(1,1) NOT NULL,
	[mensaje] [varchar](100) NULL,
	[encabezado] [varchar](50) NOT NULL,
	[fechaHora] [datetime] NOT NULL,
	[idUsuario] [int] NOT NULL,
	[idCisterna] [int] NOT NULL,
 CONSTRAINT [PK_notificaciones] PRIMARY KEY CLUSTERED 
(
	[idNotificacion] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[planta]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[planta](
	[idPlanta] [int] IDENTITY(1,1) NOT NULL,
	[nombreCorto] [varchar](50) NULL,
	[descripcion] [varchar](250) NULL,
	[ubicacion] [varchar](100) NULL,
 CONSTRAINT [PK_planta] PRIMARY KEY CLUSTERED 
(
	[idPlanta] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[registroActividad]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[registroActividad](
	[idRegistroActividad] [int] IDENTITY(1,1) NOT NULL,
	[accion] [varchar](50) NOT NULL,
	[fechaHora] [datetime] NOT NULL,
	[idUsuario] [int] NOT NULL,
 CONSTRAINT [PK_registroActividad] PRIMARY KEY CLUSTERED 
(
	[idRegistroActividad] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[usuarioCisterna]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[usuarioCisterna](
	[idUsuarioCisterna] [int] IDENTITY(1,1) NOT NULL,
	[idUsuario] [int] NOT NULL,
	[idCisterna] [int] NOT NULL,
 CONSTRAINT [PK_Table_1] PRIMARY KEY CLUSTERED 
(
	[idUsuarioCisterna] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[usuarios]    Script Date: 28-Mar-23 1:09:21 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[usuarios](
	[idUsuario] [int] IDENTITY(1,1) NOT NULL,
	[nombreCompleto] [varchar](100) NOT NULL,
	[usuario] [varchar](50) NOT NULL,
	[contrasena] [varchar](50) NOT NULL,
	[correo] [varchar](50) NOT NULL,
	[fechaAlta] [datetime] NOT NULL,
 CONSTRAINT [PK_usuarios] PRIMARY KEY CLUSTERED 
(
	[idUsuario] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[alertas]  WITH CHECK ADD  CONSTRAINT [FK_alertas_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[alertas] CHECK CONSTRAINT [FK_alertas_cisternas]
GO
ALTER TABLE [dbo].[cisternaPlanta]  WITH CHECK ADD  CONSTRAINT [FK_cisternaPlanta_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[cisternaPlanta] CHECK CONSTRAINT [FK_cisternaPlanta_cisternas]
GO
ALTER TABLE [dbo].[cisternaPlanta]  WITH CHECK ADD  CONSTRAINT [FK_cisternaPlanta_planta] FOREIGN KEY([idPlanta])
REFERENCES [dbo].[planta] ([idPlanta])
GO
ALTER TABLE [dbo].[cisternaPlanta] CHECK CONSTRAINT [FK_cisternaPlanta_planta]
GO
ALTER TABLE [dbo].[cisternas]  WITH CHECK ADD  CONSTRAINT [FK_cisternas_ajustesCuenta] FOREIGN KEY([idAjuste])
REFERENCES [dbo].[ajustesCuenta] ([idAjuste])
GO
ALTER TABLE [dbo].[cisternas] CHECK CONSTRAINT [FK_cisternas_ajustesCuenta]
GO
ALTER TABLE [dbo].[configuracionesAlertas]  WITH CHECK ADD  CONSTRAINT [FK_configuracionesAlertas_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[configuracionesAlertas] CHECK CONSTRAINT [FK_configuracionesAlertas_cisternas]
GO
ALTER TABLE [dbo].[configuracionesAlertas]  WITH CHECK ADD  CONSTRAINT [FK_configuracionesAlertas_frecuenciaAlertas] FOREIGN KEY([idFrecuenciaAlertas])
REFERENCES [dbo].[frecuenciaAlertas] ([idFrecuenciaAlertas])
GO
ALTER TABLE [dbo].[configuracionesAlertas] CHECK CONSTRAINT [FK_configuracionesAlertas_frecuenciaAlertas]
GO
ALTER TABLE [dbo].[historial]  WITH CHECK ADD  CONSTRAINT [FK_historial_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[historial] CHECK CONSTRAINT [FK_historial_cisternas]
GO
ALTER TABLE [dbo].[historialLog]  WITH CHECK ADD  CONSTRAINT [FK_historialLog_usuarios] FOREIGN KEY([idUsuario])
REFERENCES [dbo].[usuarios] ([idUsuario])
GO
ALTER TABLE [dbo].[historialLog] CHECK CONSTRAINT [FK_historialLog_usuarios]
GO
ALTER TABLE [dbo].[mediciones]  WITH CHECK ADD  CONSTRAINT [FK_mediciones_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[mediciones] CHECK CONSTRAINT [FK_mediciones_cisternas]
GO
ALTER TABLE [dbo].[movimientoCisterna]  WITH CHECK ADD  CONSTRAINT [FK_movimientoCisterna_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[movimientoCisterna] CHECK CONSTRAINT [FK_movimientoCisterna_cisternas]
GO
ALTER TABLE [dbo].[movimientoCisterna]  WITH CHECK ADD  CONSTRAINT [FK_movimientoCisterna_usuarios] FOREIGN KEY([idUsuario])
REFERENCES [dbo].[usuarios] ([idUsuario])
GO
ALTER TABLE [dbo].[movimientoCisterna] CHECK CONSTRAINT [FK_movimientoCisterna_usuarios]
GO
ALTER TABLE [dbo].[notificaciones]  WITH CHECK ADD  CONSTRAINT [FK_notificaciones_usuarios] FOREIGN KEY([idUsuario])
REFERENCES [dbo].[usuarios] ([idUsuario])
GO
ALTER TABLE [dbo].[notificaciones] CHECK CONSTRAINT [FK_notificaciones_usuarios]
GO
ALTER TABLE [dbo].[registroActividad]  WITH CHECK ADD  CONSTRAINT [FK_registroActividad_usuarios] FOREIGN KEY([idUsuario])
REFERENCES [dbo].[usuarios] ([idUsuario])
GO
ALTER TABLE [dbo].[registroActividad] CHECK CONSTRAINT [FK_registroActividad_usuarios]
GO
ALTER TABLE [dbo].[usuarioCisterna]  WITH CHECK ADD  CONSTRAINT [FK_Table_1_cisternas] FOREIGN KEY([idCisterna])
REFERENCES [dbo].[cisternas] ([idCisterna])
GO
ALTER TABLE [dbo].[usuarioCisterna] CHECK CONSTRAINT [FK_Table_1_cisternas]
GO
ALTER TABLE [dbo].[usuarioCisterna]  WITH CHECK ADD  CONSTRAINT [FK_Table_1_usuarios] FOREIGN KEY([idUsuario])
REFERENCES [dbo].[usuarios] ([idUsuario])
GO
ALTER TABLE [dbo].[usuarioCisterna] CHECK CONSTRAINT [FK_Table_1_usuarios]
GO
