USE [master]
GO
/****** Object:  Database [ParkingManagementSystem]    Script Date: 04/06/2026 1:41:05 SA ******/
CREATE DATABASE [ParkingManagementSystem]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'ParkingManagementSystem', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\ParkingManagementSystem.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'ParkingManagementSystem_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\ParkingManagementSystem_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [ParkingManagementSystem] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [ParkingManagementSystem].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [ParkingManagementSystem] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET ARITHABORT OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [ParkingManagementSystem] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [ParkingManagementSystem] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [ParkingManagementSystem] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET  ENABLE_BROKER 
GO
ALTER DATABASE [ParkingManagementSystem] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [ParkingManagementSystem] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [ParkingManagementSystem] SET  MULTI_USER 
GO
ALTER DATABASE [ParkingManagementSystem] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [ParkingManagementSystem] SET DB_CHAINING OFF 
GO
ALTER DATABASE [ParkingManagementSystem] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [ParkingManagementSystem] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [ParkingManagementSystem] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [ParkingManagementSystem] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [ParkingManagementSystem] SET QUERY_STORE = ON
GO
ALTER DATABASE [ParkingManagementSystem] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [ParkingManagementSystem]
GO
/****** Object:  Table [dbo].[Account]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Account](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[username] [varchar](255) NULL,
	[password] [varchar](255) NOT NULL,
	[email] [varchar](255) NULL,
	[phone] [varchar](255) NULL,
	[role] [varchar](255) NULL,
	[building_id] [int] NULL,
	[status] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Booking]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Booking](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NOT NULL,
	[building_id] [int] NOT NULL,
	[vehicle_type_id] [int] NOT NULL,
	[license_plate] [varchar](255) NULL,
	[start_time] [datetime] NOT NULL,
	[end_time] [datetime] NOT NULL,
	[status] [varchar](255) NULL,
	[created_at] [datetime] NULL,
	[cancelled_at] [datetime] NULL,
	[expired_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FeedbackTicket]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FeedbackTicket](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NOT NULL,
	[title] [nvarchar](255) NOT NULL,
	[description] [nvarchar](max) NOT NULL,
	[status] [varchar](50) NULL,
	[resolved_by] [int] NULL,
	[created_at] [datetime] NULL,
	[resolved_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Floor]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Floor](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[building_id] [int] NULL,
	[name] [varchar](255) NULL,
	[floor_level] [int] NOT NULL,
	[capacity] [int] NOT NULL,
	[vehicle_type_id] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[MonthlyTicket]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[MonthlyTicket](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NULL,
	[vehicle_type_id] [int] NULL,
	[license_plate] [varchar](50) NOT NULL,
	[start_date] [date] NOT NULL,
	[end_date] [date] NOT NULL,
	[status] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ParkingBuilding]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ParkingBuilding](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](255) NOT NULL,
	[address] [varchar](255) NULL,
	[status] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ParkingCard]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ParkingCard](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[card_code] [varchar](100) NOT NULL,
	[status] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ParkingSession]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ParkingSession](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NULL,
	[building_id] [int] NOT NULL,
	[slot_id] [int] NULL,
	[vehicle_type_id] [int] NULL,
	[card_id] [int] NULL,
	[license_plate] [varchar](255) NULL,
	[entry_gate] [varchar](255) NULL,
	[exit_gate] [varchar](255) NULL,
	[check_in_time] [datetime] NULL,
	[check_out_time] [datetime] NULL,
	[status] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ParkingZone]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ParkingZone](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[floor_id] [int] NULL,
	[name] [varchar](255) NULL,
	[vehicle_type_id] [int] NULL,
	[capacity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Payment]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Payment](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[session_id] [int] NULL,
	[monthly_ticket_id] [int] NULL,
	[amount] [decimal](12, 2) NOT NULL,
	[payment_method] [varchar](50) NULL,
	[status] [varchar](50) NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PricingBlock]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PricingBlock](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[policy_id] [int] NULL,
	[block_order] [int] NOT NULL,
	[duration_hours] [int] NOT NULL,
	[price] [float] NULL,
	[pricing_policy_id] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PricingPolicy]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PricingPolicy](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](255) NOT NULL,
	[vehicle_type_id] [int] NULL,
	[status] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Slot]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Slot](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[zone_id] [int] NULL,
	[name] [varchar](255) NULL,
	[status] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SystemConfig]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SystemConfig](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[config_key] [varchar](100) NOT NULL,
	[config_value] [nvarchar](500) NOT NULL,
	[description] [nvarchar](max) NULL,
	[building_id] [int] NULL,
	[updated_by] [int] NULL,
	[updated_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SystemLog]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SystemLog](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[log_type] [varchar](50) NOT NULL,
	[message] [nvarchar](max) NOT NULL,
	[created_by] [int] NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[VehicleType]    Script Date: 04/06/2026 1:41:05 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VehicleType](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NULL,
	[description] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[Account] ON 

INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (1, N'admin1', N'123456', N'admin@test.com', N'0000000000', N'ADMIN', NULL, N'ACTIVE')
INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (20, N'manager1', N'123456', N'manager@test.com', N'0123456789', N'MANAGER', 1, N'ACTIVE')
INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (21, N'staff1', N'123456', N'staff@test.com', N'0123456788', N'STAFF', 1, N'ACTIVE')
INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (22, N'customer1', N'123456', N'customer@test.com', N'0123456787', N'USER', NULL, N'ACTIVE')
INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (23, N'test', N'123', N'nguyenhuyhoangnle@gmail.com', N'028 7300 5588', N'STAFF', 1, N'ACTIVE')
INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (25, N'test1', N'123', N'nguyenhuyhoangnle@gmail.com', N'028 7300 5588', N'MANAGER', 1, N'ACTIVE')
INSERT [dbo].[Account] ([id], [username], [password], [email], [phone], [role], [building_id], [status]) VALUES (27, N'testtd', N'1', N'nguyenhuyhoangnle@gmailcom', N'1', N'STAFF', 1, N'ACTIVE')
SET IDENTITY_INSERT [dbo].[Account] OFF
GO
SET IDENTITY_INSERT [dbo].[Booking] ON 

INSERT [dbo].[Booking] ([id], [account_id], [building_id], [vehicle_type_id], [license_plate], [start_time], [end_time], [status], [created_at], [cancelled_at], [expired_at]) VALUES (3, 22, 1, 1, N'29A-TEST1', CAST(N'2026-06-04T01:57:06.000' AS DateTime), CAST(N'2026-06-04T03:57:06.000' AS DateTime), N'CANCELLED', CAST(N'2026-06-04T00:57:06.350' AS DateTime), CAST(N'2026-06-04T00:57:06.437' AS DateTime), NULL)
INSERT [dbo].[Booking] ([id], [account_id], [building_id], [vehicle_type_id], [license_plate], [start_time], [end_time], [status], [created_at], [cancelled_at], [expired_at]) VALUES (4, 22, 1, 1, N'29A-TEST2', CAST(N'2026-06-04T00:17:15.430' AS DateTime), CAST(N'2026-06-04T03:57:06.000' AS DateTime), N'EXPIRED', CAST(N'2026-06-04T00:57:06.407' AS DateTime), NULL, CAST(N'2026-06-04T00:57:20.893' AS DateTime))
INSERT [dbo].[Booking] ([id], [account_id], [building_id], [vehicle_type_id], [license_plate], [start_time], [end_time], [status], [created_at], [cancelled_at], [expired_at]) VALUES (5, 20, 1, 1, N'30A-99999', CAST(N'2026-06-05T08:00:00.000' AS DateTime), CAST(N'2026-06-06T18:00:00.000' AS DateTime), N'CANCELLED', CAST(N'2026-06-04T01:03:21.503' AS DateTime), CAST(N'2026-06-04T01:03:26.600' AS DateTime), NULL)
INSERT [dbo].[Booking] ([id], [account_id], [building_id], [vehicle_type_id], [license_plate], [start_time], [end_time], [status], [created_at], [cancelled_at], [expired_at]) VALUES (6, 20, 1, 1, N'51A-12345', CAST(N'2026-06-04T03:07:00.000' AS DateTime), CAST(N'2026-06-04T05:08:00.000' AS DateTime), N'CANCELLED', CAST(N'2026-06-04T01:08:03.993' AS DateTime), CAST(N'2026-06-04T01:10:06.820' AS DateTime), NULL)
SET IDENTITY_INSERT [dbo].[Booking] OFF
GO
SET IDENTITY_INSERT [dbo].[Floor] ON 

INSERT [dbo].[Floor] ([id], [building_id], [name], [floor_level], [capacity], [vehicle_type_id]) VALUES (1, 1, N'Floor 1', 1, 100, NULL)
INSERT [dbo].[Floor] ([id], [building_id], [name], [floor_level], [capacity], [vehicle_type_id]) VALUES (2, 1, N'Floor 2', 2, 100, NULL)
SET IDENTITY_INSERT [dbo].[Floor] OFF
GO
SET IDENTITY_INSERT [dbo].[ParkingBuilding] ON 

INSERT [dbo].[ParkingBuilding] ([id], [name], [address], [status]) VALUES (1, N'Building A', N'123 Main St', N'OPEN')
INSERT [dbo].[ParkingBuilding] ([id], [name], [address], [status]) VALUES (2, N'Building B', N'456 Side St', N'OPEN')
SET IDENTITY_INSERT [dbo].[ParkingBuilding] OFF
GO
SET IDENTITY_INSERT [dbo].[ParkingZone] ON 

INSERT [dbo].[ParkingZone] ([id], [floor_id], [name], [vehicle_type_id], [capacity]) VALUES (1, 1, N'Zone A1', 1, 50)
INSERT [dbo].[ParkingZone] ([id], [floor_id], [name], [vehicle_type_id], [capacity]) VALUES (2, 2, N'Zone B1', 2, 50)
SET IDENTITY_INSERT [dbo].[ParkingZone] OFF
GO
SET IDENTITY_INSERT [dbo].[Slot] ON 

INSERT [dbo].[Slot] ([id], [zone_id], [name], [status]) VALUES (1, 1, N'A1-01', N'EMPTY')
INSERT [dbo].[Slot] ([id], [zone_id], [name], [status]) VALUES (2, 1, N'A1-02', N'EMPTY')
INSERT [dbo].[Slot] ([id], [zone_id], [name], [status]) VALUES (3, 2, N'B1-01', N'EMPTY')
INSERT [dbo].[Slot] ([id], [zone_id], [name], [status]) VALUES (4, 2, N'B1-02', N'EMPTY')
SET IDENTITY_INSERT [dbo].[Slot] OFF
GO
SET IDENTITY_INSERT [dbo].[VehicleType] ON 

INSERT [dbo].[VehicleType] ([id], [name], [description]) VALUES (1, N'Car', N'Standard car')
INSERT [dbo].[VehicleType] ([id], [name], [description]) VALUES (2, N'Motorbike', N'Standard motorbike')
SET IDENTITY_INSERT [dbo].[VehicleType] OFF
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__Account__F3DBC572E8489692]    Script Date: 04/06/2026 1:41:05 SA ******/
ALTER TABLE [dbo].[Account] ADD UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__ParkingC__81703D72F859EDA0]    Script Date: 04/06/2026 1:41:05 SA ******/
ALTER TABLE [dbo].[ParkingCard] ADD UNIQUE NONCLUSTERED 
(
	[card_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__SystemCo__BDF6033DEDCB42B7]    Script Date: 04/06/2026 1:41:05 SA ******/
ALTER TABLE [dbo].[SystemConfig] ADD UNIQUE NONCLUSTERED 
(
	[config_key] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT ('ACTIVE') FOR [status]
GO
ALTER TABLE [dbo].[Booking] ADD  DEFAULT ('CONFIRMED') FOR [status]
GO
ALTER TABLE [dbo].[Booking] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[FeedbackTicket] ADD  DEFAULT ('OPEN') FOR [status]
GO
ALTER TABLE [dbo].[FeedbackTicket] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[Floor] ADD  DEFAULT ((0)) FOR [capacity]
GO
ALTER TABLE [dbo].[MonthlyTicket] ADD  DEFAULT ('ACTIVE') FOR [status]
GO
ALTER TABLE [dbo].[ParkingBuilding] ADD  DEFAULT ('OPEN') FOR [status]
GO
ALTER TABLE [dbo].[ParkingCard] ADD  DEFAULT ('AVAILABLE') FOR [status]
GO
ALTER TABLE [dbo].[ParkingSession] ADD  DEFAULT ('ACTIVE') FOR [status]
GO
ALTER TABLE [dbo].[ParkingZone] ADD  DEFAULT ((0)) FOR [capacity]
GO
ALTER TABLE [dbo].[Payment] ADD  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[Payment] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[PricingPolicy] ADD  DEFAULT ('ACTIVE') FOR [status]
GO
ALTER TABLE [dbo].[Slot] ADD  DEFAULT ('EMPTY') FOR [status]
GO
ALTER TABLE [dbo].[SystemConfig] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[SystemLog] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[Account]  WITH CHECK ADD FOREIGN KEY([building_id])
REFERENCES [dbo].[ParkingBuilding] ([id])
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([building_id])
REFERENCES [dbo].[ParkingBuilding] ([id])
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([vehicle_type_id])
REFERENCES [dbo].[VehicleType] ([id])
GO
ALTER TABLE [dbo].[FeedbackTicket]  WITH CHECK ADD FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[FeedbackTicket]  WITH CHECK ADD FOREIGN KEY([resolved_by])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[Floor]  WITH CHECK ADD FOREIGN KEY([building_id])
REFERENCES [dbo].[ParkingBuilding] ([id])
GO
ALTER TABLE [dbo].[Floor]  WITH CHECK ADD  CONSTRAINT [FKj6emenmhgsdfjh1aoop8tynwe] FOREIGN KEY([vehicle_type_id])
REFERENCES [dbo].[VehicleType] ([id])
GO
ALTER TABLE [dbo].[Floor] CHECK CONSTRAINT [FKj6emenmhgsdfjh1aoop8tynwe]
GO
ALTER TABLE [dbo].[MonthlyTicket]  WITH CHECK ADD FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[MonthlyTicket]  WITH CHECK ADD FOREIGN KEY([vehicle_type_id])
REFERENCES [dbo].[VehicleType] ([id])
GO
ALTER TABLE [dbo].[ParkingSession]  WITH CHECK ADD FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[ParkingSession]  WITH CHECK ADD FOREIGN KEY([building_id])
REFERENCES [dbo].[ParkingBuilding] ([id])
GO
ALTER TABLE [dbo].[ParkingSession]  WITH CHECK ADD FOREIGN KEY([card_id])
REFERENCES [dbo].[ParkingCard] ([id])
GO
ALTER TABLE [dbo].[ParkingSession]  WITH CHECK ADD FOREIGN KEY([slot_id])
REFERENCES [dbo].[Slot] ([id])
GO
ALTER TABLE [dbo].[ParkingSession]  WITH CHECK ADD FOREIGN KEY([vehicle_type_id])
REFERENCES [dbo].[VehicleType] ([id])
GO
ALTER TABLE [dbo].[ParkingZone]  WITH CHECK ADD FOREIGN KEY([floor_id])
REFERENCES [dbo].[Floor] ([id])
GO
ALTER TABLE [dbo].[ParkingZone]  WITH CHECK ADD FOREIGN KEY([vehicle_type_id])
REFERENCES [dbo].[VehicleType] ([id])
GO
ALTER TABLE [dbo].[Payment]  WITH CHECK ADD FOREIGN KEY([monthly_ticket_id])
REFERENCES [dbo].[MonthlyTicket] ([id])
GO
ALTER TABLE [dbo].[Payment]  WITH CHECK ADD FOREIGN KEY([session_id])
REFERENCES [dbo].[ParkingSession] ([id])
GO
ALTER TABLE [dbo].[PricingBlock]  WITH CHECK ADD FOREIGN KEY([policy_id])
REFERENCES [dbo].[PricingPolicy] ([id])
GO
ALTER TABLE [dbo].[PricingBlock]  WITH CHECK ADD  CONSTRAINT [FKdod6tuti1ighrvehx3vs3axai] FOREIGN KEY([pricing_policy_id])
REFERENCES [dbo].[PricingPolicy] ([id])
GO
ALTER TABLE [dbo].[PricingBlock] CHECK CONSTRAINT [FKdod6tuti1ighrvehx3vs3axai]
GO
ALTER TABLE [dbo].[PricingPolicy]  WITH CHECK ADD FOREIGN KEY([vehicle_type_id])
REFERENCES [dbo].[VehicleType] ([id])
GO
ALTER TABLE [dbo].[Slot]  WITH CHECK ADD FOREIGN KEY([zone_id])
REFERENCES [dbo].[ParkingZone] ([id])
GO
ALTER TABLE [dbo].[SystemConfig]  WITH CHECK ADD FOREIGN KEY([building_id])
REFERENCES [dbo].[ParkingBuilding] ([id])
GO
ALTER TABLE [dbo].[SystemConfig]  WITH CHECK ADD FOREIGN KEY([updated_by])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[SystemLog]  WITH CHECK ADD FOREIGN KEY([created_by])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[Account]  WITH CHECK ADD CHECK  (([role]='USER' OR [role]='STAFF' OR [role]='MANAGER' OR [role]='ADMIN'))
GO
ALTER TABLE [dbo].[Account]  WITH CHECK ADD CHECK  (([status]='BANNED' OR [status]='INACTIVE' OR [status]='ACTIVE'))
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD CHECK  (([status]='USED' OR [status]='EXPIRED' OR [status]='CANCELLED' OR [status]='CONFIRMED'))
GO
ALTER TABLE [dbo].[FeedbackTicket]  WITH CHECK ADD CHECK  (([status]='CLOSED' OR [status]='RESOLVED' OR [status]='IN_PROGRESS' OR [status]='OPEN'))
GO
ALTER TABLE [dbo].[MonthlyTicket]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='EXPIRED' OR [status]='ACTIVE'))
GO
ALTER TABLE [dbo].[ParkingBuilding]  WITH CHECK ADD CHECK  (([status]='CLOSED' OR [status]='OPEN'))
GO
ALTER TABLE [dbo].[ParkingCard]  WITH CHECK ADD CHECK  (([status]='BLOCKED' OR [status]='LOST' OR [status]='IN_USE' OR [status]='AVAILABLE'))
GO
ALTER TABLE [dbo].[ParkingSession]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='COMPLETED' OR [status]='ACTIVE' OR [status]='RESERVED'))
GO
ALTER TABLE [dbo].[Payment]  WITH CHECK ADD CHECK  (([payment_method]='MOMO' OR [payment_method]='VNPAY' OR [payment_method]='CASH'))
GO
ALTER TABLE [dbo].[Payment]  WITH CHECK ADD CHECK  (([status]='FAILED' OR [status]='SUCCESS' OR [status]='PENDING'))
GO
ALTER TABLE [dbo].[PricingPolicy]  WITH CHECK ADD CHECK  (([status]='INACTIVE' OR [status]='ACTIVE'))
GO
ALTER TABLE [dbo].[Slot]  WITH CHECK ADD CHECK  (([status]='MAINTENANCE' OR [status]='OCCUPIED' OR [status]='EMPTY'))
GO
ALTER TABLE [dbo].[SystemLog]  WITH CHECK ADD CHECK  (([log_type]='SLOT_HISTORY' OR [log_type]='AUDIT' OR [log_type]='ERROR'))
GO
USE [master]
GO
ALTER DATABASE [ParkingManagementSystem] SET  READ_WRITE 
GO
