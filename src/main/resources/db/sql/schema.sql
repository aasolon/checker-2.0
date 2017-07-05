CREATE TABLE Task (
  Id IDENTITY,
  Name VARCHAR(255) NOT NULL,
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
  Token UUID NOT NULL,
  Judge_ID VARCHAR(255) NOT NULL,
  OosKey BIGINT
);

CREATE TABLE Language (
  Id IDENTITY,
  Name VARCHAR(255) NOT NULL,
  OosKey BIGINT
);

CREATE TABLE Result (
  Id IDENTITY,
  Task_Id BIGINT NOT NULL CONSTRAINT fk_Result_Task REFERENCES Task,
  Team_Id BIGINT NOT NULL CONSTRAINT fk_Result_Team REFERENCES Team,
  Tstamp TIMESTAMP NOT NULL,
  Lang_Id BIGINT NOT NULL CONSTRAINT fk_Result_Lang REFERENCES Language,
  Verdict VARCHAR(255),
  CompilationError CLOB,
  SourceCode CLOB NOT NULL,
  TestNumber INT,
  Runtime DECIMAL,
  Memory VARCHAR(255),
  OosKey BIGINT,
  DisabledForTest BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE TestCase (
  Id IDENTITY,
  Tstamp TIMESTAMP NOT NULL,
  Team_Id BIGINT NOT NULL CONSTRAINT fk_TestCase_Team REFERENCES Team,
  Task_Id BIGINT NOT NULL CONSTRAINT fk_TestCase_Task REFERENCES Task,
  Input CLOB NOT NULL,
  Actual BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE TestCaseResult (
  Id IDENTITY,
  TestCase_Id BIGINT NOT NULL CONSTRAINT fk_TestCaseResult_TestCase REFERENCES TestCase,
  Result_Id BIGINT NOT NULL CONSTRAINT fk_TestCaseResult_Result REFERENCES Result,
  Output CLOB,
  CONSTRAINT uq_TestCaseResult UNIQUE(TestCase_Id, Result_Id)
);

CREATE TABLE TestVerdict (
  Id IDENTITY,
  Team_Id BIGINT NOT NULL CONSTRAINT fk_TestVerdict_Team REFERENCES Team,
  Task_Id BIGINT NOT NULL CONSTRAINT fk_TestVerdict_Task REFERENCES Task,
  Result_Id BIGINT NOT NULL CONSTRAINT fk_TestVerdict_Result REFERENCES Result,
  Verdict BOOLEAN,
  CONSTRAINT uq_TestVerdict UNIQUE(Team_Id, Task_Id, Result_Id)
);