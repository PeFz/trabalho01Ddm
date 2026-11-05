# PlantCare – Santuário Botânico 🌿
**Trabalho da Disciplina (T1) – Desenvolvimento para Dispositivos Móveis (UDESC Alto Vale)**  
**Professor:** Mattheus da Hora França  
**Tecnologia:** Android Nativo (Java) • Arquitetura MVVM • Room SQLite • Retrofit 2 • Sensores • Câmera • Serviço em Background

---

## 1. Roteiro de Apresentação e Documentação do Projeto

### Introdução e Motivação
O cuidado com plantas domésticas em apartamentos e residências urbanas cresce continuamente, mas muitos cultivadores esquecem ciclos de rega, adubação ou posicionam espécies em locais com luminosidade inadequada. O **PlantCare (Santuário Botânico)** nasce com a motivação de transformar a rotina de cuidado botânico em um ritual simples, visual e apoiado pelos sensores e recursos nativos do smartphone.

### Objetivo Geral e Funcionalidades (Requisitos Funcionais)
Desenvolver um aplicativo Android nativo em **Java** que auxilie o jardineiro no gerenciamento completo de suas plantas domésticas.
- **RF01:** Listar as plantas do usuário com filtro dinâmico (`Todas`, `Precisam de água`, `Hidratadas`), barra de hidratação e ação rápida de rega.
- **RF02:** Cadastrar novas plantas informando foto real capturada pela **Câmera** (ou ilustração botânica), espécie (`Spinner` / `AutoCompleteTextView`), apelido afetivo (`EditText`), cômodo e data da última rega.
- **RF03:** Visualizar os detalhes vitais de cada planta (frequência, luminosidade, umidade, toxicidade pet, sensor de substrato) e o **Diário de Cuidados** cronológico.
- **RF04:** Registrar novos eventos no Diário de Cuidados (regas, adubação NPK, podas, troca de vaso) persistidos localmente via **Room (SQLite)**.
- **RF05:** Medir a luminosidade real do ambiente em Lux utilizando o **Sensor de Luz (`Sensor.TYPE_LIGHT`)** do aparelho e sincronizar dados meteorológicos locais (temperatura e umidade do ar) em tempo real via **Retrofit (API REST Open-Meteo)**.
- **RF06:** Monitorar em segundo plano as plantas que necessitam de água por meio de um **Serviço em Background (`WateringReminderService`)** com disparo de notificações.

---

## 2. Mapeamento dos Requisitos do Edital (T1)

### Fluxo de 6 Telas (Fragments + Bottom Navigation)
1. **Tela 1 – Minhas Plantas (`HomeFragment`)**: Saudação personalizada, calendário semanal, card de lembrete de rega com resumo climático da API, filtros em pílula e `RecyclerView` com `PlantAdapter`.
2. **Tela 2 – Adicionar Nova Planta (`AddPlantFragment`)**: Captura de foto via **Câmera**, seleção de espécie (`Spinner` e `AutoCompleteTextView`), cômodo, data da última rega, `CheckBox`, `ProgressDialog`, `Toast` e `RecyclerView` horizontal de Espécies Populares (`PopularSpeciesAdapter`).
3. **Tela 3 – Detalhes da Planta (`PlantDetailFragment`)**: Card Hero com foto e favorito (`ImageButton`), alerta de hidratação, botão "Marcar como Regada Hoje", grid 2x2 de Necessidades Vitais, Sensor de Substrato, timeline do Diário de Cuidados (`RecyclerView` com `CareLogAdapter`) e Guia Rápido de Diagnóstico.
4. **Tela 4 – Medidor de Luz & Clima (`LightSensorFragment`)**: Leitura em tempo real do sensor óptico `Sensor.TYPE_LIGHT` em Lux (com modo simulador via `SeekBar` e `ToggleButton` para testes em emulador) e consumo da API REST Open-Meteo via **Retrofit**.
5. **Tela 5 – Registrar Cuidado no Diário (`AddCareLogFragment`)**: Formulário com `Spinner` de categoria, `EditText` de observações, `ToggleButton` de drenagem e `CheckBox` de atualização de hidratação no banco **Room**.
6. **Tela 6 – Catálogo de Espécies & Regras (`SpeciesCatalogFragment`)**: Busca com `AutoCompleteTextView`, catálogo em `RecyclerView` (`CatalogSpeciesAdapter`) e lista de Regras de Ouro utilizando `ListView` e `ArrayAdapter`.
7. **Tela 7 – Configurações & Serviço (`SettingsFragment`)**: Edição do perfil do jardineiro, estatísticas do banco e controle/teste imediato do `WateringReminderService`.

### Persistência e Integrações (2 Opções Implementadas)
- **Room (SQLite):** `PlantDatabase`, `PlantDao`, `CareLogDao`, `PlantEntity` e `CareLogEntity` (pré-populado automaticamente na primeira execução com as 4 plantas do protótipo: *Costela-de-Adão*, *Zamioculca*, *Samambaia* e *Cacto Ouriço*).
- **Retrofit 2 + Gson:** `RetrofitClient`, `WeatherApiService` e `WeatherResponse` consumindo `https://api.open-meteo.com/v1/forecast`.

### Recursos do Dispositivo (3 Itens Implementados)
- **Câmera:** Captura de foto da planta no cadastro e na tela de detalhes, persistindo o arquivo JPEG no armazenamento interno (`PlantImageHelper`).
- **Sensores:** Uso do `SensorManager` e `Sensor.TYPE_LIGHT` na tela `LightSensorFragment`.
- **Serviços em Background:** `WateringReminderService` verificando plantas com hidratação crítica em background thread e emitindo notificações com `NotificationCompat`.

