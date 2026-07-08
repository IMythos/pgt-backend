-- QA seed data for local Docker/PostgreSQL API testing.
-- Execute after the microservices have started at least once, because tables are
-- created/validated by the services and are not managed by PostgreSQL init SQL.
--
-- Sedes logicas usadas por el proyecto:
--   1 = Lima
--   2 = Miraflores
--
-- Credenciales QA:
--   qa.admin / Qa123456!
--   qa.almacen.lima / Qa123456!
--   qa.almacen.miraflores / Qa123456!

BEGIN;

CREATE SCHEMA IF NOT EXISTS administration;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS tracking;

-- Administration: roles, users, accounts.
INSERT INTO administration.rol (id_rol, nombre_rol, fec_creacion)
VALUES
    (9001, 'ADMIN', CURRENT_TIMESTAMP),
    (9002, 'ALMACEN', CURRENT_TIMESTAMP)
ON CONFLICT (id_rol) DO UPDATE
SET nombre_rol = EXCLUDED.nombre_rol;

INSERT INTO administration.usuario (id_usuario, usuario_uuid, nombre, apellido, dni, fec_creacion, actualizado_en)
VALUES
    (9001, '00000000-0000-0000-0000-000000009001', 'QA', 'Administrador', '99009001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9002, '00000000-0000-0000-0000-000000009002', 'Carlos', 'Almacen Lima', '99009002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9003, '00000000-0000-0000-0000-000000009003', 'Mariana', 'Almacen Miraflores', '99009003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO UPDATE
SET usuario_uuid = EXCLUDED.usuario_uuid,
    nombre = EXCLUDED.nombre,
    apellido = EXCLUDED.apellido,
    dni = EXCLUDED.dni,
    actualizado_en = CURRENT_TIMESTAMP;

INSERT INTO administration.usuario_rol (id_usuario, id_rol)
VALUES
    (9001, 9001),
    (9002, 9002),
    (9003, 9002)
ON CONFLICT DO NOTHING;

INSERT INTO administration.cuenta_usuario (id_cuenta, cuenta_uuid, id_usuario, id_sede, nom_usu, "contraseña", estado, fec_creacion)
VALUES
    (9001, '00000000-0000-0000-0000-000000019001', 9001, 1, 'qa.admin', '$2a$10$j6QOgzcmXsnvSmYmYnNZkOoeASB8/mxVrU5bun7TcuNJ/fjEuoDlK', true, CURRENT_TIMESTAMP),
    (9002, '00000000-0000-0000-0000-000000019002', 9002, 1, 'qa.almacen.lima', '$2a$10$j6QOgzcmXsnvSmYmYnNZkOoeASB8/mxVrU5bun7TcuNJ/fjEuoDlK', true, CURRENT_TIMESTAMP),
    (9003, '00000000-0000-0000-0000-000000019003', 9003, 2, 'qa.almacen.miraflores', '$2a$10$j6QOgzcmXsnvSmYmYnNZkOoeASB8/mxVrU5bun7TcuNJ/fjEuoDlK', true, CURRENT_TIMESTAMP)
ON CONFLICT (id_cuenta) DO UPDATE
SET cuenta_uuid = EXCLUDED.cuenta_uuid,
    id_usuario = EXCLUDED.id_usuario,
    id_sede = EXCLUDED.id_sede,
    nom_usu = EXCLUDED.nom_usu,
    "contraseña" = EXCLUDED."contraseña",
    estado = EXCLUDED.estado;

-- Inventory: categories and brands.
INSERT INTO inventory.categoria (id_categoria, cot_cat, nom_cat)
SELECT id_categoria, cot_cat, nom_cat
FROM (VALUES
    (9001, 'LAPTOPS', 'Laptops'),
    (9002, 'CARGADORES', 'Cargadores de laptop'),
    (9003, 'BATERIAS', 'Baterias de laptop'),
    (9004, 'PANTALLAS', 'Pantallas de laptop'),
    (9005, 'TECLADOS', 'Teclados de laptop')
    ) AS seed(id_categoria, cot_cat, nom_cat)
WHERE NOT EXISTS (
    SELECT 1
    FROM inventory.categoria c
    WHERE c.cot_cat = seed.cot_cat
)
ON CONFLICT (id_categoria) DO UPDATE
SET cot_cat = EXCLUDED.cot_cat,
    nom_cat = EXCLUDED.nom_cat;

INSERT INTO inventory.marca (id_marca, nombre)
VALUES
    (9001, 'HP'),
    (9002, 'LENOVO'),
    (9003, 'TOSHIBA'),
    (9004, 'DELL'),
    (9005, 'ASUS'),
    (9006, 'ACER'),
    (9007, 'APPLE'),
    (9008, 'DYNABOOK')
ON CONFLICT (nombre) DO NOTHING;

-- Inventory: warehouses and locations.
INSERT INTO inventory.almacen (id_almacen, id_sede, almacen_uuid, cod_alm, nombre, tipo, activo)
VALUES
    (9101, 1, '00000000-0000-0000-0000-000000029101', 'LIM-CPZ', 'Almacen Compuplaza', 'PRINCIPAL', true),
    (9102, 1, '00000000-0000-0000-0000-000000029102', 'LIM-CYP', 'Almacen Cyberplaza', 'PRINCIPAL', true),
    (9103, 1, '00000000-0000-0000-0000-000000029103', 'LIM-CWL', 'Almacen Compuwilson', 'PRINCIPAL', true),
    (9201, 2, '00000000-0000-0000-0000-000000029201', 'MIR-TEC', 'Almacen Miraflores', 'PRINCIPAL', true)
ON CONFLICT (id_almacen) DO UPDATE
SET id_sede = EXCLUDED.id_sede,
    almacen_uuid = EXCLUDED.almacen_uuid,
    cod_alm = EXCLUDED.cod_alm,
    nombre = EXCLUDED.nombre,
    tipo = EXCLUDED.tipo,
    activo = EXCLUDED.activo;

INSERT INTO inventory.locacion (id_locacion, id_almacen, zona, pasillo, estante, cod_barras, capacidad, pos_x, pos_y, activo)
VALUES
    ('00000000-0000-0000-0000-000000039001', 9101, 'LAPTOPS', 'A', '01', 'LIM-CPZ-LAP-A01', 120, 1, 1, true),
    ('00000000-0000-0000-0000-000000039002', 9101, 'CARGADORES', 'B', '01', 'LIM-CPZ-CAR-B01', 250, 2, 1, true),
    ('00000000-0000-0000-0000-000000039003', 9101, 'BATERIAS', 'C', '01', 'LIM-CPZ-BAT-C01', 180, 3, 1, true),
    ('00000000-0000-0000-0000-000000039004', 9102, 'PANTALLAS', 'A', '01', 'LIM-CYP-PAN-A01', 100, 1, 2, true),
    ('00000000-0000-0000-0000-000000039005', 9102, 'TECLADOS', 'B', '01', 'LIM-CYP-TEC-B01', 220, 2, 2, true),
    ('00000000-0000-0000-0000-000000039006', 9103, 'LAPTOPS', 'A', '01', 'LIM-CWL-LAP-A01', 100, 1, 3, true),
    ('00000000-0000-0000-0000-000000039007', 9103, 'REPUESTOS', 'B', '01', 'LIM-CWL-REP-B01', 300, 2, 3, true),
    ('00000000-0000-0000-0000-000000039008', 9201, 'LAPTOPS', 'A', '01', 'MIR-TEC-LAP-A01', 80, 1, 4, true),
    ('00000000-0000-0000-0000-000000039009', 9201, 'REPUESTOS', 'B', '01', 'MIR-TEC-REP-B01', 180, 2, 4, true)
ON CONFLICT (id_locacion) DO UPDATE
SET id_almacen = EXCLUDED.id_almacen,
    zona = EXCLUDED.zona,
    pasillo = EXCLUDED.pasillo,
    estante = EXCLUDED.estante,
    cod_barras = EXCLUDED.cod_barras,
    capacidad = EXCLUDED.capacidad,
    pos_x = EXCLUDED.pos_x,
    pos_y = EXCLUDED.pos_y,
    activo = EXCLUDED.activo;

-- Inventory: technology products. modelos_compatibles is a JSONB array of strings
-- because the backend maps it as List<String>.
INSERT INTO inventory.producto (
    id_producto,
    id_categoria,
    id_marca,
    cod_prod,
    cod_anexo,
    descripcion,
    modelos_compatibles,
    pre_com,
    pre_ven,
    estado,
    fec_creacion,
    stock_minimo
)
VALUES
    ('00000000-0000-0000-0000-000000049001', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'LAPTOPS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'HP'), 'QA-LAP-HP-001', 'HP-PB450G9', 'Laptop HP ProBook 450 G9 Core i5 8GB 512GB SSD', '["HP ProBook 450 G8", "HP ProBook 450 G9", "HP ProBook 455 G9", "PN 6A180LT", "PN 6A181LT"]'::jsonb, 2450.0000, 2999.0000, true, CURRENT_TIMESTAMP, 3),
    ('00000000-0000-0000-0000-000000049002', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'LAPTOPS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'LENOVO'), 'QA-LAP-LEN-001', 'LEN-T14G2', 'Laptop Lenovo ThinkPad T14 Gen 2 Core i7 16GB 512GB SSD', '["Lenovo ThinkPad T14 Gen 1", "Lenovo ThinkPad T14 Gen 2", "Lenovo ThinkPad E14", "PN 20W000SPLM", "PN 20W00159LM"]'::jsonb, 3200.0000, 3899.0000, true, CURRENT_TIMESTAMP, 2),
    ('00000000-0000-0000-0000-000000049003', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'LAPTOPS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'DELL'), 'QA-LAP-DELL-001', 'DEL-LAT5420', 'Laptop Dell Latitude 5420 Core i5 16GB 512GB SSD', '["Dell Latitude 5420", "Dell Latitude 5430", "Dell Latitude 5520", "PN 0M5Y1K", "PN 0V0P8H"]'::jsonb, 2850.0000, 3499.0000, true, CURRENT_TIMESTAMP, 2),
    ('00000000-0000-0000-0000-000000049004', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'LAPTOPS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'ASUS'), 'QA-LAP-ASUS-001', 'ASU-VB15', 'Laptop ASUS VivoBook 15 Ryzen 5 8GB 512GB SSD', '["ASUS VivoBook 15 X515", "ASUS VivoBook 15 M1502", "ASUS X515EA", "PN 90NB0TY1", "PN 90NB0VX2"]'::jsonb, 2050.0000, 2599.0000, true, CURRENT_TIMESTAMP, 3),
    ('00000000-0000-0000-0000-000000049005', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'LAPTOPS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'ACER'), 'QA-LAP-ACER-001', 'ACE-AS5', 'Laptop Acer Aspire 5 Core i5 8GB 512GB SSD', '["Acer Aspire 5 A515-54", "Acer Aspire 5 A515-55", "Acer Aspire 5 A515-56", "PN NX.HS5AL", "PN NX.A1SAL"]'::jsonb, 1950.0000, 2399.0000, true, CURRENT_TIMESTAMP, 3),
    ('00000000-0000-0000-0000-000000049006', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'LAPTOPS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'APPLE'), 'QA-LAP-APP-001', 'APP-MBA-M1', 'MacBook Air 13 M1 8GB 256GB SSD', '["Apple MacBook Air 13 M1 A2337", "Apple MacBook Air 13 2020", "PN MGN63LL/A", "PN MGN93LL/A"]'::jsonb, 3600.0000, 4299.0000, true, CURRENT_TIMESTAMP, 1),
    ('00000000-0000-0000-0000-000000049007', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'CARGADORES'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'HP'), 'QA-CHA-HP-001', 'HP-65W-AZUL', 'Cargador HP 19.5V 3.33A 65W punta azul', '["HP Pavilion 14-ce", "HP Pavilion 14-cf", "HP Pavilion 15-cs", "HP ProBook 440 G6", "PN L2345-001", "PN L2346-001"]'::jsonb, 55.0000, 89.0000, true, CURRENT_TIMESTAMP, 10),
    ('00000000-0000-0000-0000-000000049008', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'CARGADORES'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'LENOVO'), 'QA-CHA-LEN-001', 'LEN-USBC-65W', 'Cargador Lenovo USB-C 65W original', '["Lenovo ThinkPad T14", "Lenovo ThinkPad E14", "Lenovo IdeaPad 3", "Lenovo Yoga Slim 7", "PN 4X20M26268", "PN 5A10W86240"]'::jsonb, 75.0000, 119.0000, true, CURRENT_TIMESTAMP, 12),
    ('00000000-0000-0000-0000-000000049009', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'CARGADORES'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'DELL'), 'QA-CHA-DELL-001', 'DEL-65W-7450', 'Cargador Dell 19.5V 3.34A 65W punta 7.4mm', '["Dell Latitude 5420", "Dell Latitude 5520", "Dell Inspiron 15 3501", "Dell Vostro 3400", "PN LA65NS2-01", "PN HA65NM130"]'::jsonb, 70.0000, 109.0000, true, CURRENT_TIMESTAMP, 12),
    ('00000000-0000-0000-0000-000000049010', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'CARGADORES'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'ASUS'), 'QA-CHA-ASUS-001', 'ASU-65W-5525', 'Cargador ASUS 19V 3.42A 65W punta 5.5mm', '["ASUS VivoBook 15 X515", "ASUS X441", "ASUS X541", "ASUS A556U", "PN ADP-65GD B", "PN AD883020"]'::jsonb, 58.0000, 95.0000, true, CURRENT_TIMESTAMP, 10),
    ('00000000-0000-0000-0000-000000049011', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'CARGADORES'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'APPLE'), 'QA-CHA-APP-001', 'APP-USBC-61W', 'Cargador Apple USB-C 61W para MacBook', '["Apple MacBook Pro 13 A1706", "Apple MacBook Pro 13 A1708", "Apple MacBook Air A2337", "PN A1947", "PN MRW22LL/A"]'::jsonb, 145.0000, 219.0000, true, CURRENT_TIMESTAMP, 5),
    ('00000000-0000-0000-0000-000000049012', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'BATERIAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'HP'), 'QA-BAT-HP-001', 'HP-HS04', 'Bateria HP HS04 4 celdas', '["HP Pavilion 14-ac", "HP Pavilion 15-ac", "HP 240 G4", "HP 245 G4", "PN 807957-001", "PN 807956-001"]'::jsonb, 95.0000, 149.0000, true, CURRENT_TIMESTAMP, 8),
    ('00000000-0000-0000-0000-000000049013', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'BATERIAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'LENOVO'), 'QA-BAT-LEN-001', 'LEN-L17M3P52', 'Bateria Lenovo L17M3P52 ThinkPad', '["Lenovo ThinkPad E480", "Lenovo ThinkPad E490", "Lenovo ThinkPad E590", "PN L17M3P52", "PN 01AV445"]'::jsonb, 130.0000, 199.0000, true, CURRENT_TIMESTAMP, 6),
    ('00000000-0000-0000-0000-000000049014', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'BATERIAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'DELL'), 'QA-BAT-DELL-001', 'DEL-E5450', 'Bateria Dell Latitude E5450 51Wh', '["Dell Latitude E5450", "Dell Latitude E5550", "Dell Latitude 3450", "PN G5M10", "PN R9XM9"]'::jsonb, 125.0000, 189.0000, true, CURRENT_TIMESTAMP, 6),
    ('00000000-0000-0000-0000-000000049015', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'BATERIAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'ASUS'), 'QA-BAT-ASUS-001', 'ASU-A32K55', 'Bateria ASUS A32-K55 6 celdas', '["ASUS K55A", "ASUS K55V", "ASUS A45", "ASUS A55", "PN A32-K55", "PN A41-K55"]'::jsonb, 105.0000, 165.0000, true, CURRENT_TIMESTAMP, 6),
    ('00000000-0000-0000-0000-000000049016', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'PANTALLAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'DYNABOOK'), 'QA-PAN-SLIM14-001', 'LCD-14-30P-FHD', 'Pantalla LED 14.0 Slim 30 pines Full HD', '["HP EliteBook 840 G5", "Lenovo ThinkPad T14", "Dell Latitude 5420", "Dynabook Tecra A40", "PN N140HCA-EAC", "PN B140HAN04.0"]'::jsonb, 180.0000, 279.0000, true, CURRENT_TIMESTAMP, 5),
    ('00000000-0000-0000-0000-000000049017', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'PANTALLAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'DYNABOOK'), 'QA-PAN-SLIM156-001', 'LCD-156-30P-FHD', 'Pantalla LED 15.6 Slim 30 pines Full HD', '["HP Pavilion 15-cs", "ASUS VivoBook 15 X515", "Acer Aspire 5 A515", "Dell Inspiron 15 3501", "PN N156HCA-EAB", "PN B156HAN02.1"]'::jsonb, 170.0000, 259.0000, true, CURRENT_TIMESTAMP, 5),
    ('00000000-0000-0000-0000-000000049018', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'PANTALLAS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'APPLE'), 'QA-PAN-APP-001', 'LCD-MBA-A2337', 'Pantalla LED 13.3 MacBook Air A2337', '["Apple MacBook Air 13 M1 A2337", "Apple MacBook Air 13 2020", "PN 661-15397", "PN 661-15398"]'::jsonb, 680.0000, 899.0000, true, CURRENT_TIMESTAMP, 2),
    ('00000000-0000-0000-0000-000000049019', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'TECLADOS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'HP'), 'QA-TEC-HP-001', 'KB-HP-PB450-ES', 'Teclado HP ProBook 450 español', '["HP ProBook 450 G6", "HP ProBook 450 G7", "HP ProBook 450 G8", "PN L01028-161", "PN L01029-161"]'::jsonb, 75.0000, 129.0000, true, CURRENT_TIMESTAMP, 8),
    ('00000000-0000-0000-0000-000000049020', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'TECLADOS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'LENOVO'), 'QA-TEC-LEN-001', 'KB-LEN-T14-ES', 'Teclado Lenovo ThinkPad T14 español', '["Lenovo ThinkPad T14 Gen 1", "Lenovo ThinkPad T14 Gen 2", "Lenovo ThinkPad P14s", "PN 5N20V43775", "PN 5N20V43776"]'::jsonb, 95.0000, 155.0000, true, CURRENT_TIMESTAMP, 6),
    ('00000000-0000-0000-0000-000000049021', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'TECLADOS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'DELL'), 'QA-TEC-DELL-001', 'KB-DELL-LAT-ES', 'Teclado Dell Latitude español', '["Dell Latitude 5420", "Dell Latitude 5430", "Dell Latitude 5520", "PN 0M8F00", "PN 0CJ2K4"]'::jsonb, 88.0000, 145.0000, true, CURRENT_TIMESTAMP, 6),
    ('00000000-0000-0000-0000-000000049022', (SELECT id_categoria FROM inventory.categoria WHERE cot_cat = 'TECLADOS'), (SELECT id_marca FROM inventory.marca WHERE nombre = 'ASUS'), 'QA-TEC-ASUS-001', 'KB-ASUS-VIVO-ES', 'Teclado ASUS VivoBook español', '["ASUS VivoBook 15 X515", "ASUS X515EA", "ASUS M515", "PN 0KNB0-662PLA00", "PN 0KNB0-662PUS00"]'::jsonb, 78.0000, 135.0000, true, CURRENT_TIMESTAMP, 6)
ON CONFLICT (id_producto) DO UPDATE
SET id_categoria = EXCLUDED.id_categoria,
    id_marca = EXCLUDED.id_marca,
    cod_prod = EXCLUDED.cod_prod,
    cod_anexo = EXCLUDED.cod_anexo,
    descripcion = EXCLUDED.descripcion,
    modelos_compatibles = EXCLUDED.modelos_compatibles,
    pre_com = EXCLUDED.pre_com,
    pre_ven = EXCLUDED.pre_ven,
    estado = EXCLUDED.estado,
    stock_minimo = EXCLUDED.stock_minimo;

-- Inventory: lots. One lot per product distributed across Lima and Miraflores warehouses.
INSERT INTO inventory.lote (id_lote, id_producto, id_locacion, nro_lote, fec_ingreso, costo_unit, estado, proveedor, cod_prov, cantidad)
VALUES
    ('00000000-0000-0000-0000-000000059001', '00000000-0000-0000-0000-000000049001', '00000000-0000-0000-0000-000000039001', 'LOT-HP-PB450G9-001', CURRENT_DATE, 2450.0000, 'DISPONIBLE', 'Ingram Micro Peru', 'ING-HP-001', 12),
    ('00000000-0000-0000-0000-000000059002', '00000000-0000-0000-0000-000000049002', '00000000-0000-0000-0000-000000039006', 'LOT-LEN-T14G2-001', CURRENT_DATE, 3200.0000, 'DISPONIBLE', 'Intcomex Peru', 'INT-LEN-001', 8),
    ('00000000-0000-0000-0000-000000059003', '00000000-0000-0000-0000-000000049003', '00000000-0000-0000-0000-000000039008', 'LOT-DEL-LAT5420-001', CURRENT_DATE, 2850.0000, 'DISPONIBLE', 'Deltron', 'DEL-DELL-001', 7),
    ('00000000-0000-0000-0000-000000059004', '00000000-0000-0000-0000-000000049004', '00000000-0000-0000-0000-000000039001', 'LOT-ASU-VB15-001', CURRENT_DATE, 2050.0000, 'DISPONIBLE', 'Memory Kings', 'MEM-ASUS-001', 15),
    ('00000000-0000-0000-0000-000000059005', '00000000-0000-0000-0000-000000049005', '00000000-0000-0000-0000-000000039006', 'LOT-ACE-AS5-001', CURRENT_DATE, 1950.0000, 'DISPONIBLE', 'Tech Data Peru', 'TDP-ACER-001', 14),
    ('00000000-0000-0000-0000-000000059006', '00000000-0000-0000-0000-000000049006', '00000000-0000-0000-0000-000000039008', 'LOT-APP-MBA-M1-001', CURRENT_DATE, 3600.0000, 'DISPONIBLE', 'Apple Authorized Distributor', 'AAD-APP-001', 5),
    ('00000000-0000-0000-0000-000000059007', '00000000-0000-0000-0000-000000049007', '00000000-0000-0000-0000-000000039002', 'LOT-HP-65W-001', CURRENT_DATE, 55.0000, 'DISPONIBLE', 'Ingram Micro Peru', 'ING-HP-CHA-001', 50),
    ('00000000-0000-0000-0000-000000059008', '00000000-0000-0000-0000-000000049008', '00000000-0000-0000-0000-000000039002', 'LOT-LEN-USBC65-001', CURRENT_DATE, 75.0000, 'DISPONIBLE', 'Intcomex Peru', 'INT-LEN-CHA-001', 45),
    ('00000000-0000-0000-0000-000000059009', '00000000-0000-0000-0000-000000049009', '00000000-0000-0000-0000-000000039007', 'LOT-DELL-65W-001', CURRENT_DATE, 70.0000, 'DISPONIBLE', 'Deltron', 'DEL-DELL-CHA-001', 40),
    ('00000000-0000-0000-0000-000000059010', '00000000-0000-0000-0000-000000049010', '00000000-0000-0000-0000-000000039009', 'LOT-ASUS-65W-001', CURRENT_DATE, 58.0000, 'DISPONIBLE', 'Memory Kings', 'MEM-ASUS-CHA-001', 38),
    ('00000000-0000-0000-0000-000000059011', '00000000-0000-0000-0000-000000049011', '00000000-0000-0000-0000-000000039009', 'LOT-APP-61W-001', CURRENT_DATE, 145.0000, 'DISPONIBLE', 'Apple Authorized Distributor', 'AAD-APP-CHA-001', 18),
    ('00000000-0000-0000-0000-000000059012', '00000000-0000-0000-0000-000000049012', '00000000-0000-0000-0000-000000039003', 'LOT-HP-HS04-001', CURRENT_DATE, 95.0000, 'DISPONIBLE', 'Ingram Micro Peru', 'ING-HP-BAT-001', 28),
    ('00000000-0000-0000-0000-000000059013', '00000000-0000-0000-0000-000000049013', '00000000-0000-0000-0000-000000039003', 'LOT-LEN-L17M3P52-001', CURRENT_DATE, 130.0000, 'DISPONIBLE', 'Intcomex Peru', 'INT-LEN-BAT-001', 22),
    ('00000000-0000-0000-0000-000000059014', '00000000-0000-0000-0000-000000049014', '00000000-0000-0000-0000-000000039007', 'LOT-DELL-E5450-001', CURRENT_DATE, 125.0000, 'DISPONIBLE', 'Deltron', 'DEL-DELL-BAT-001', 20),
    ('00000000-0000-0000-0000-000000059015', '00000000-0000-0000-0000-000000049015', '00000000-0000-0000-0000-000000039009', 'LOT-ASUS-A32K55-001', CURRENT_DATE, 105.0000, 'DISPONIBLE', 'Memory Kings', 'MEM-ASUS-BAT-001', 18),
    ('00000000-0000-0000-0000-000000059016', '00000000-0000-0000-0000-000000049016', '00000000-0000-0000-0000-000000039004', 'LOT-LCD14-FHD-001', CURRENT_DATE, 180.0000, 'DISPONIBLE', 'Importaciones Wilson', 'IW-PAN-14-001', 16),
    ('00000000-0000-0000-0000-000000059017', '00000000-0000-0000-0000-000000049017', '00000000-0000-0000-0000-000000039004', 'LOT-LCD156-FHD-001', CURRENT_DATE, 170.0000, 'DISPONIBLE', 'Importaciones Wilson', 'IW-PAN-156-001', 24),
    ('00000000-0000-0000-0000-000000059018', '00000000-0000-0000-0000-000000049018', '00000000-0000-0000-0000-000000039009', 'LOT-LCD-MBA-A2337-001', CURRENT_DATE, 680.0000, 'DISPONIBLE', 'Apple Authorized Distributor', 'AAD-APP-PAN-001', 6),
    ('00000000-0000-0000-0000-000000059019', '00000000-0000-0000-0000-000000049019', '00000000-0000-0000-0000-000000039005', 'LOT-KB-HP-PB450-001', CURRENT_DATE, 75.0000, 'DISPONIBLE', 'Importaciones Wilson', 'IW-TEC-HP-001', 30),
    ('00000000-0000-0000-0000-000000059020', '00000000-0000-0000-0000-000000049020', '00000000-0000-0000-0000-000000039005', 'LOT-KB-LEN-T14-001', CURRENT_DATE, 95.0000, 'DISPONIBLE', 'Intcomex Peru', 'INT-LEN-TEC-001', 22),
    ('00000000-0000-0000-0000-000000059021', '00000000-0000-0000-0000-000000049021', '00000000-0000-0000-0000-000000039007', 'LOT-KB-DELL-LAT-001', CURRENT_DATE, 88.0000, 'DISPONIBLE', 'Deltron', 'DEL-DELL-TEC-001', 25),
    ('00000000-0000-0000-0000-000000059022', '00000000-0000-0000-0000-000000049022', '00000000-0000-0000-0000-000000039009', 'LOT-KB-ASUS-VIVO-001', CURRENT_DATE, 78.0000, 'DISPONIBLE', 'Memory Kings', 'MEM-ASUS-TEC-001', 24)
ON CONFLICT (id_lote) DO UPDATE
SET id_producto = EXCLUDED.id_producto,
    id_locacion = EXCLUDED.id_locacion,
    nro_lote = EXCLUDED.nro_lote,
    fec_ingreso = EXCLUDED.fec_ingreso,
    costo_unit = EXCLUDED.costo_unit,
    estado = EXCLUDED.estado,
    proveedor = EXCLUDED.proveedor,
    cod_prov = EXCLUDED.cod_prov,
    cantidad = EXCLUDED.cantidad;

-- Inventory: initial stock movements and kardex rows.
INSERT INTO inventory.movimiento (id_movimiento, id_lote, id_usuario, tipo, fecha, motivo, doc_ref, cantidad)
SELECT
    ('00000000-0000-0000-0000-00000006' || LPAD(seq::text, 4, '0'))::uuid,
    id_lote,
    9002,
    'ENTRADA',
    CURRENT_TIMESTAMP,
    'Stock inicial de tecnologia QA',
    'QA-SEED-TECH-001',
    cantidad
FROM (
    VALUES
        (9001, '00000000-0000-0000-0000-000000059001'::uuid, 12),
        (9002, '00000000-0000-0000-0000-000000059002'::uuid, 8),
        (9003, '00000000-0000-0000-0000-000000059003'::uuid, 7),
        (9004, '00000000-0000-0000-0000-000000059004'::uuid, 15),
        (9005, '00000000-0000-0000-0000-000000059005'::uuid, 14),
        (9006, '00000000-0000-0000-0000-000000059006'::uuid, 5),
        (9007, '00000000-0000-0000-0000-000000059007'::uuid, 50),
        (9008, '00000000-0000-0000-0000-000000059008'::uuid, 45),
        (9009, '00000000-0000-0000-0000-000000059009'::uuid, 40),
        (9010, '00000000-0000-0000-0000-000000059010'::uuid, 38),
        (9011, '00000000-0000-0000-0000-000000059011'::uuid, 18),
        (9012, '00000000-0000-0000-0000-000000059012'::uuid, 28),
        (9013, '00000000-0000-0000-0000-000000059013'::uuid, 22),
        (9014, '00000000-0000-0000-0000-000000059014'::uuid, 20),
        (9015, '00000000-0000-0000-0000-000000059015'::uuid, 18),
        (9016, '00000000-0000-0000-0000-000000059016'::uuid, 16),
        (9017, '00000000-0000-0000-0000-000000059017'::uuid, 24),
        (9018, '00000000-0000-0000-0000-000000059018'::uuid, 6),
        (9019, '00000000-0000-0000-0000-000000059019'::uuid, 30),
        (9020, '00000000-0000-0000-0000-000000059020'::uuid, 22),
        (9021, '00000000-0000-0000-0000-000000059021'::uuid, 25),
        (9022, '00000000-0000-0000-0000-000000059022'::uuid, 24)
) AS seed(seq, id_lote, cantidad)
ON CONFLICT (id_movimiento) DO UPDATE
SET id_lote = EXCLUDED.id_lote,
    id_usuario = EXCLUDED.id_usuario,
    tipo = EXCLUDED.tipo,
    fecha = EXCLUDED.fecha,
    motivo = EXCLUDED.motivo,
    doc_ref = EXCLUDED.doc_ref,
    cantidad = EXCLUDED.cantidad;

INSERT INTO inventory.kardex (id_kardex, id_movimiento, id_producto, stock_anterior, cant_ingreso, cant_salida, stock_actual, costo_prom)
SELECT
    ('00000000-0000-0000-0000-00000007' || LPAD(seq::text, 4, '0'))::uuid,
    ('00000000-0000-0000-0000-00000006' || LPAD(seq::text, 4, '0'))::uuid,
    id_producto,
    0,
    cantidad,
    0,
    cantidad,
    costo_unit
FROM (
    VALUES
        (9001, '00000000-0000-0000-0000-000000049001'::uuid, 12, 2450.0000),
        (9002, '00000000-0000-0000-0000-000000049002'::uuid, 8, 3200.0000),
        (9003, '00000000-0000-0000-0000-000000049003'::uuid, 7, 2850.0000),
        (9004, '00000000-0000-0000-0000-000000049004'::uuid, 15, 2050.0000),
        (9005, '00000000-0000-0000-0000-000000049005'::uuid, 14, 1950.0000),
        (9006, '00000000-0000-0000-0000-000000049006'::uuid, 5, 3600.0000),
        (9007, '00000000-0000-0000-0000-000000049007'::uuid, 50, 55.0000),
        (9008, '00000000-0000-0000-0000-000000049008'::uuid, 45, 75.0000),
        (9009, '00000000-0000-0000-0000-000000049009'::uuid, 40, 70.0000),
        (9010, '00000000-0000-0000-0000-000000049010'::uuid, 38, 58.0000),
        (9011, '00000000-0000-0000-0000-000000049011'::uuid, 18, 145.0000),
        (9012, '00000000-0000-0000-0000-000000049012'::uuid, 28, 95.0000),
        (9013, '00000000-0000-0000-0000-000000049013'::uuid, 22, 130.0000),
        (9014, '00000000-0000-0000-0000-000000049014'::uuid, 20, 125.0000),
        (9015, '00000000-0000-0000-0000-000000049015'::uuid, 18, 105.0000),
        (9016, '00000000-0000-0000-0000-000000049016'::uuid, 16, 180.0000),
        (9017, '00000000-0000-0000-0000-000000049017'::uuid, 24, 170.0000),
        (9018, '00000000-0000-0000-0000-000000049018'::uuid, 6, 680.0000),
        (9019, '00000000-0000-0000-0000-000000049019'::uuid, 30, 75.0000),
        (9020, '00000000-0000-0000-0000-000000049020'::uuid, 22, 95.0000),
        (9021, '00000000-0000-0000-0000-000000049021'::uuid, 25, 88.0000),
        (9022, '00000000-0000-0000-0000-000000049022'::uuid, 24, 78.0000)
) AS seed(seq, id_producto, cantidad, costo_unit)
ON CONFLICT (id_kardex) DO UPDATE
SET id_movimiento = EXCLUDED.id_movimiento,
    id_producto = EXCLUDED.id_producto,
    stock_anterior = EXCLUDED.stock_anterior,
    cant_ingreso = EXCLUDED.cant_ingreso,
    cant_salida = EXCLUDED.cant_salida,
    stock_actual = EXCLUDED.stock_actual,
    costo_prom = EXCLUDED.costo_prom;

-- Tracking: pending picking order with real technology items.
INSERT INTO tracking.orden_pick (id_orden, usuario_picking_id, estado, fec_creacion, fec_inicio, fec_fin)
VALUES (
    '00000000-0000-0000-0000-000000089001',
    9002,
    'PENDIENTE',
    CURRENT_TIMESTAMP,
    NULL,
    NULL
)
ON CONFLICT (id_orden) DO UPDATE
SET usuario_picking_id = EXCLUDED.usuario_picking_id,
    estado = EXCLUDED.estado,
    fec_creacion = EXCLUDED.fec_creacion,
    fec_inicio = EXCLUDED.fec_inicio,
    fec_fin = EXCLUDED.fec_fin;

INSERT INTO tracking.detalle_pick (id_detalle, id_orden, producto_id, locacion_id, cant_requerida, cant_seleccion, estado)
VALUES
    ('00000000-0000-0000-0000-000000099001', '00000000-0000-0000-0000-000000089001', '00000000-0000-0000-0000-000000049008', '00000000-0000-0000-0000-000000039002', 2, 0, 'PENDIENTE'),
    ('00000000-0000-0000-0000-000000099002', '00000000-0000-0000-0000-000000089001', '00000000-0000-0000-0000-000000049012', '00000000-0000-0000-0000-000000039003', 1, 0, 'PENDIENTE'),
    ('00000000-0000-0000-0000-000000099003', '00000000-0000-0000-0000-000000089001', '00000000-0000-0000-0000-000000049021', '00000000-0000-0000-0000-000000039007', 1, 0, 'PENDIENTE')
ON CONFLICT (id_detalle) DO UPDATE
SET id_orden = EXCLUDED.id_orden,
    producto_id = EXCLUDED.producto_id,
    locacion_id = EXCLUDED.locacion_id,
    cant_requerida = EXCLUDED.cant_requerida,
    cant_seleccion = EXCLUDED.cant_seleccion,
    estado = EXCLUDED.estado;

INSERT INTO tracking.rutas_pick (id_ruta, id_orden, path_seq, distancia_estimada, fec_creacion)
VALUES (
    '00000000-0000-0000-0000-000000109001',
    '00000000-0000-0000-0000-000000089001',
    '["00000000-0000-0000-0000-000000039002", "00000000-0000-0000-0000-000000039003", "00000000-0000-0000-0000-000000039007"]'::jsonb,
    18.50,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id_ruta) DO UPDATE
SET id_orden = EXCLUDED.id_orden,
    path_seq = EXCLUDED.path_seq,
    distancia_estimada = EXCLUDED.distancia_estimada,
    fec_creacion = EXCLUDED.fec_creacion;

INSERT INTO tracking.auditoria_pick (id_auditoria, fecha_evento, id_usuario, entidad_tab, entidad_id, accion, ip_origen, ms_origen, valores_anteriores, valores_nuevos)
VALUES (
    '00000000-0000-0000-0000-000000119001',
    CURRENT_TIMESTAMP,
    9002,
    'orden_pick',
    '00000000-0000-0000-0000-000000089001',
    'QA_SEED',
    '127.0.0.1',
    'seed-qa-data',
    '{}'::jsonb,
    '{"estado":"PENDIENTE","docRef":"QA-SEED-TECH-001"}'::jsonb
)
ON CONFLICT (id_auditoria) DO UPDATE
SET fecha_evento = EXCLUDED.fecha_evento,
    id_usuario = EXCLUDED.id_usuario,
    entidad_tab = EXCLUDED.entidad_tab,
    entidad_id = EXCLUDED.entidad_id,
    accion = EXCLUDED.accion,
    ip_origen = EXCLUDED.ip_origen,
    ms_origen = EXCLUDED.ms_origen,
    valores_anteriores = EXCLUDED.valores_anteriores,
    valores_nuevos = EXCLUDED.valores_nuevos;

SELECT setval(pg_get_serial_sequence('administration.rol', 'id_rol'), GREATEST(9002, COALESCE((SELECT MAX(id_rol) FROM administration.rol), 1)), true);
SELECT setval(pg_get_serial_sequence('administration.usuario', 'id_usuario'), GREATEST(9003, COALESCE((SELECT MAX(id_usuario) FROM administration.usuario), 1)), true);
SELECT setval(pg_get_serial_sequence('administration.cuenta_usuario', 'id_cuenta'), GREATEST(9003, COALESCE((SELECT MAX(id_cuenta) FROM administration.cuenta_usuario), 1)), true);
SELECT setval(pg_get_serial_sequence('inventory.categoria', 'id_categoria'), GREATEST(9005, COALESCE((SELECT MAX(id_categoria) FROM inventory.categoria), 1)), true);
SELECT setval(pg_get_serial_sequence('inventory.marca', 'id_marca'), GREATEST(9008, COALESCE((SELECT MAX(id_marca) FROM inventory.marca), 1)), true);
SELECT setval(pg_get_serial_sequence('inventory.almacen', 'id_almacen'), GREATEST(9201, COALESCE((SELECT MAX(id_almacen) FROM inventory.almacen), 1)), true);

COMMIT;
