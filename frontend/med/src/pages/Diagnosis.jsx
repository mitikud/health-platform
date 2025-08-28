import React, { useState } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";

function Diagnosis() {
  const { t } = useTranslation();
  const [symptom, setSymptom] = useState("");
  const [result, setResult] = useState(null);

  const analyze = async () => {
    const res = await axios.post("/api/diagnosis/analyze", { text: symptom });
    setResult(res.data);
  };

  return (
    <div>
      <h2>{t("diagnosis")}</h2>
      <input value={symptom} onChange={(e) => setSymptom(e.target.value)} />
      <button onClick={analyze}>{t("analyze")}</button>
      {result && <p>{t("result")}: {result.diagnosis}</p>}
    </div>
  );
}

export default Diagnosis;
