CREATE OR REPLACE FUNCTION bonus_calculation(idUsuario integer)
RETURNS TABLE(LOWER_LIMIT FLOAT, UPPER_LIMIT FLOAT, BONUS FLOAT) AS
$$
DECLARE
  number_valid_of_investments  INTEGER;
  average                FLOAT;
  standard_desviation    FLOAT;
  cv_interval            RECORD;
  cv                     FLOAT;
  investment_interval    RECORD;
BEGIN
  SELECT COUNT(*) INTO number_valid_of_investments FROM USERS U, INVESTMENTS I WHERE U.ID = I.USER_ID AND U.ID = idUsuario AND I.AMOUNT - I.WALLET_AMOUNT > 3000;

   IF number_valid_of_investments = 0 THEN
    return QUERY SELECT NULL::FLOAT, NULL::FLOAT, NULL::FLOAT;
   END IF;
   --average
   SELECT AVG(I.AMOUNT - I.WALLET_AMOUNT) INTO average FROM USERS U, INVESTMENTS I WHERE U.ID = I.USER_ID AND U.ID = idUsuario AND I.AMOUNT - I.WALLET_AMOUNT > 3000;
   -- standard_desviation
   SELECT SQRT(SUM(POW(I.AMOUNT - I.WALLET_AMOUNT - (SELECT AVG(I.AMOUNT - I.WALLET_AMOUNT) FROM USERS U, INVESTMENTS I WHERE U.ID = I.USER_ID AND U.ID = idUsuario AND I.AMOUNT - I.WALLET_AMOUNT > 3000),2))/number_valid_of_investments) INTO standard_desviation FROM USERS U, INVESTMENTS I WHERE U.ID = I.USER_ID AND U.ID = idUsuario AND I.AMOUNT - I.WALLET_AMOUNT > 3000;
   --cv_interval
   cv := standard_desviation/average;
   SELECT * INTO cv_interval FROM CV_INTERVALS CI WHERE (cv > MIN AND cv <= MAX) OR(cv > MIN AND MAX IS NULL) OR (MIN IS NULL AND cv <= MAX) LIMIT 1;
   --investment_interval
   SELECT * INTO investment_interval FROM INVESTMENT_INTERVALS WHERE ((average BETWEEN MIN AND MAX) OR (MIN IS NULL AND average < MAX) OR (MIN < average AND MAX IS NULL)) AND COLLECTION = 1 LIMIT 1;

   IF CV_INTERVAL IS NULL THEN
    --standard_desviation
    SELECT stdv INTO standard_desviation FROM UNIT_BONUS WHERE INVESTMENT_INTERVAL_ID = investment_interval.id;
    --cv_interval
    cv := 0.31;
    SELECT * INTO cv_interval FROM CV_INTERVALS CI WHERE (cv > MIN AND cv <= MAX) OR(cv > MIN AND MAX IS NULL) OR (MIN IS NULL AND cv <= MAX) LIMIT 1;
   END IF;
   RETURN QUERY SELECT CAST(TO_CHAR(B.STD * standard_desviation::FLOAT,'FM999999990.00') AS FLOAT) LOWER_LIMIT,CAST(TO_CHAR(((SELECT MIN(B2.STD) FROM BONUS B2 WHERE B2.CV_INVESTMENT_INTERVAL_ID = (SELECT ID FROM CV_INVESTMENT_INTERVALS WHERE CV_INTERVAL_ID = cv_interval.id AND INVESTMENT_INTERVAL_ID = investment_interval.id) AND B2.STD > B.STD) * standard_desviation - 0.01)::FLOAT,'FM999999990.00') AS FLOAT) UPPER_LIMIT, B.PERCENT BONUS FROM BONUS B WHERE B.cv_investment_interval_id = (SELECT ID FROM CV_INVESTMENT_INTERVALS WHERE CV_INTERVAL_ID = cv_interval.id AND INVESTMENT_INTERVAL_ID = investment_interval.id) ORDER BY B.STD;
END
$$
LANGUAGE plpgsql;


SELECT * FROM bonus_calculation(2);
