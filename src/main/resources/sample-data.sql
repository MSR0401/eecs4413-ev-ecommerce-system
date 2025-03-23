--
-- PostgreSQL database dump
--

-- Dumped from database version 14.17 (Homebrew)
-- Dumped by pg_dump version 14.17 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.users VALUES (1, 'Alice', 'alice@example.com', 'password123', 'customer', '2025-03-21 15:15:32.520277-04');
INSERT INTO public.users VALUES (2, 'Bob', 'bob@example.com', 'securepass', 'customer', '2025-03-21 15:15:32.520277-04');
INSERT INTO public.users VALUES (3, 'Admin', 'admin@example.com', 'adminpass', 'admin', '2025-03-21 15:15:32.520277-04');
INSERT INTO public.users VALUES (4, 'Charlie', 'charlie@example.com', 'charliepass', 'customer', '2025-03-21 15:16:36.12719-04');
INSERT INTO public.users VALUES (6, 'David', 'david@example.com', 'davidpass', 'customer', '2025-03-21 15:48:28.350162-04');
INSERT INTO public.users VALUES (10, 'Jay', 'jay@example.com', 'jaypass', 'customer', '2025-03-21 20:43:55.513967-04');


--
-- Data for Name: carts; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.carts VALUES (1, 1, '2025-03-21 15:15:32.529796-04');


--
-- Data for Name: vehicles; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.vehicles VALUES (1, 'Tesla', 'Model S', 2022, 79999.99, 100.00, 600, 'new', 5, 'https://example.com/tesla-model-s.jpg', '2025-03-21 15:15:32.523226-04');
INSERT INTO public.vehicles VALUES (2, 'Nissan', 'Leaf', 2021, 29999.99, 40.00, 300, 'new', 10, 'https://example.com/nissan-leaf.jpg', '2025-03-21 15:15:32.523226-04');


--
-- Data for Name: cart_items; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.cart_items VALUES (1, 1, 2, 1);


--
-- Data for Name: loans; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.loans VALUES (1, 1, 1, 60000.00, 5.00, 60, 1133.33, '2025-03-21 15:15:32.533927-04');


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.orders VALUES (1, 1, 79999.99, 'pending', '2025-03-21 15:15:32.525762-04');
INSERT INTO public.orders VALUES (3, 1, 79999.99, 'pending', '2025-03-21 15:50:52.330636-04');
INSERT INTO public.orders VALUES (4, 1, 79999.99, 'pending', '2025-03-21 20:29:55.459264-04');
INSERT INTO public.orders VALUES (5, 1, 79999.99, 'pending', '2025-03-21 20:43:55.612017-04');


--
-- Data for Name: order_items; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.order_items VALUES (1, 1, 1, 1, 79999.99);


--
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: mahipal
--



--
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.reports VALUES (1, 'sales', '2025-03-21 15:15:32.533096-04', 'Total sales: $79999.99, Vehicles sold: 1');


--
-- Data for Name: reviews; Type: TABLE DATA; Schema: public; Owner: mahipal
--

INSERT INTO public.reviews VALUES (1, 1, 1, 5, 'Excellent vehicle!', '2025-03-21 15:15:32.532103-04');


--
-- Name: cart_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.cart_items_id_seq', 1, true);


--
-- Name: carts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.carts_id_seq', 1, true);


--
-- Name: loans_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.loans_id_seq', 1, true);


--
-- Name: order_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.order_items_id_seq', 1, true);


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.orders_id_seq', 5, true);


--
-- Name: payments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.payments_id_seq', 1, false);


--
-- Name: reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.reports_id_seq', 1, true);


--
-- Name: reviews_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.reviews_id_seq', 1, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.users_id_seq', 11, true);


--
-- Name: vehicles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mahipal
--

SELECT pg_catalog.setval('public.vehicles_id_seq', 2, true);


--
-- PostgreSQL database dump complete
--

