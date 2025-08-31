import i18n from "i18next"
import { initReactI18next } from "react-i18next"

const resources = {
  en: {
    translation: {
      app_name: "Health Assist",
      login: "Login",
      register: "Register",
      logout: "Logout",
      language: "Language",
      submit: "Submit",
      analyze_text: "Analyze Text",
      analyze_audio: "Analyze Audio",
      analyze_image: "Analyze Image",
      symptoms_placeholder: "Describe your symptoms...",
      choose_file: "Choose file",
      diagnosis: "Diagnosis (possible)",
      recommendations: "Recommendations",
      confidence: "Confidence",
      generate_medication: "Get Medication Plan",
      tts: "Listen",
    }
  },
  am: {
    translation: {
      app_name: "የጤና አጋር",
      login: "መግባት",
      register: "መመዝገብ",
      logout: "መውጣት",
      language: "ቋንቋ",
      submit: "አስገባ",
      analyze_text: "ጽሑፍ ትንተና",
      analyze_audio: "ድምጽ ትንተና",
      analyze_image: "ምስል ትንተና",
      symptoms_placeholder: "ምልክቶችህን ግለጽ...",
      choose_file: "ፋይል ምረጥ",
      diagnosis: "ምርመራ (እንደሚቻል)",
      recommendations: "ምክሮች",
      confidence: "እምነት",
      generate_medication: "የመድሀኒት እቅድ አመንጭ",
      tts: "ስማ",
    }
  },
  ti: {
    translation: {
      app_name: "ጥዕና ጓደኛ",
      login: "መእተዊ",
      register: "ምዝገባ",
      logout: "መውጽኢ",
      language: "ቋንቋ",
      submit: "ኣቐምጥ",
      analyze_text: "ጽሑፍ ትንተና",
      analyze_audio: "ድምጺ ትንተና",
      analyze_image: "ምስል ትንተና",
      symptoms_placeholder: "ምልክታትካን መግለጺ...",
      choose_file: "ፋይል ምረፅ",
      diagnosis: "ምርመራ (ይኽእል ዝኾነ)",
      recommendations: "መመኻኸሪ",
      confidence: "ተስፋ",
      generate_medication: "መድሃኒት ሕብረት ምፍጣር",
      tts: "ስምዖ",
    }
  }
}

i18n.use(initReactI18next).init({
  resources,
  lng: "en",
  fallbackLng: "en",
  interpolation: { escapeValue: false },
})

export default i18n
