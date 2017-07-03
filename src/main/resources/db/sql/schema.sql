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
  Verdict VARCHAR(255),
  SourceCode CLOB,
  TestNumber INT,
  Runtime DECIMAL,
  Memory VARCHAR(255),
  OosKey BIGINT
);

CREATE TABLE TestCase (
  Id IDENTITY,
  Team_Id BIGINT CONSTRAINT fk_TestCase_Team REFERENCES Team,
  Task_Id BIGINT CONSTRAINT fk_TestCase_Task REFERENCES Task,
  Input CLOB
);

CREATE TABLE TestCaseResult (
  Id IDENTITY,
  Team_Id BIGINT CONSTRAINT fk_TestCaseResult_Team REFERENCES Team,
  Task_Id BIGINT CONSTRAINT fk_TestCaseResult_Task REFERENCES Task,
  TestCase_Id BIGINT CONSTRAINT fk_TestCaseResult_TestCase REFERENCES TestCase,
  Result_Id BIGINT CONSTRAINT fk_TestCaseResult_Result REFERENCES Result,
  Output CLOB
);

CREATE TABLE TestVerdict (
  Id IDENTITY,
  Team_Id BIGINT CONSTRAINT fk_TestVerdict_Team REFERENCES Team,
  Task_Id BIGINT CONSTRAINT fk_TestVerdict_Task REFERENCES Task,
  Result_Id BIGINT CONSTRAINT fk_TestVerdict_Result REFERENCES Result
);