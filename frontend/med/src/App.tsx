
import { useTranslation } from "react-i18next";
import "./i18n";

function App() {
  const { t, i18n } = useTranslation();

  return (
    <div>
      <h1>{t("welcome")}</h1>
      <button onClick={() => i18n.changeLanguage("en")}>English</button>
      <button onClick={() => i18n.changeLanguage("am")}>አማርኛ</button>
      <button onClick={() => i18n.changeLanguage("ti")}>ትግርኛ</button>
      <p>{t("diagnosis")}</p>
    </div>
  );
}

export default App;
