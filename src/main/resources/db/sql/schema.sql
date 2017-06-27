CREATE TABLE Task (
  Id IDENTITY,
  Name VARCHAR(255),
  Description CLOB,
  InitialData CLOB,
  Result CLOB,
  Example_InitialData CLOB,
  Example_Result CLOB,
  OosKey BIGINT
);

CREATE TABLE Team (
  Id IDENTITY,
  Name VARCHAR(255),
  Token UUID,
  Judge_ID VARCHAR(255),
  OosKey BIGINT
);

CREATE TABLE Language (
  Id IDENTITY,
  Name VARCHAR(255),
  OosKey BIGINT
);

CREATE TABLE Result (
  Id IDENTITY,
  Task_Id BIGINT CONSTRAINT fk_Result_Task REFERENCES Task,
  Team_Id BIGINT CONSTRAINT fk_Result_Team REFERENCES Team,
  Tstamp TIMESTAMP,
  Lang_Id BIGINT CONSTRAINT fk_Result_Lang REFERENCES Language,
  Result VARCHAR(255),
  SourceCode CLOB,
  OosKey BIGINT
);