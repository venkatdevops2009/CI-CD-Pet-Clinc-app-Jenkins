-- Pet Types
INSERT INTO pet_types (name) VALUES
('Dog'),
('Cat'),
('Bird'),
('Rabbit'),
('Hamster'),
('Lizard');

-- Specialties
INSERT INTO specialties (name) VALUES
('Radiology'),
('Surgery'),
('Dentistry');

-- Veterinarians
INSERT INTO veterinarians (first_name, last_name) VALUES
('James', 'Carter'),
('Helen', 'Leary'),
('Linda', 'Douglas');

-- Vet Specialties
INSERT INTO vet_specialties(veterinarian_id, specialty_id) VALUES
(2,1),
(3,2),
(3,3);

-- Owners
INSERT INTO owners(first_name,last_name,email,phone,address,city) VALUES
('George','Franklin','george@gmail.com','9876543210','110 W Liberty St','Madison'),
('Betty','Davis','betty@gmail.com','9876543211','1839 Matheson Ave','Madison'),
('Eduardo','Rodriquez','eduardo@gmail.com','9876543212','3220 South St','New York');

-- Pets
INSERT INTO pets(name,birth_date,owner_id,pet_type_id) VALUES
('Leo','2021-01-15',1,1),
('Bella','2022-03-12',2,2),
('Max','2020-07-20',3,1);

-- Appointments
INSERT INTO appointments(appointment_date,reason,status,pet_id) VALUES
('2026-07-15 10:00:00','Vaccination','SCHEDULED',1),
('2026-07-16 11:30:00','Regular Checkup','COMPLETED',2);

-- Health Records
INSERT INTO health_records(record_date,diagnosis,treatment,notes,pet_id) VALUES
('2026-07-01','Healthy','Vaccination','Annual vaccination completed',1),
('2026-07-02','Ear Infection','Medication','Prescribed antibiotics',2);