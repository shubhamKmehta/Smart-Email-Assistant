import { Box, Typography, TextField, Container, FormControl, InputLabel, MenuItem, Select, Button, CircularProgress } from "@mui/material";
import "./App.css";
import { useState } from "react";
import axios from "axios";

function App() {
  const [emailContent, setEmailContent] = useState('');
  const [tone,setTone] = useState('');
  const [generatedReply,setGeneratedReply] = useState('');
  const [loading , setloading] = useState(false);

  const handleSubmit = async () =>{
    setloading(true);
    try {
      const respose = await axios.post("http://localhost:8080/api/email/generate",{
        emailContent,
        tone
      });
      setGeneratedReply(typeof respose.data === "string" ? respose.data : JSON.stringify(respose.data));

    } catch (error) {
      console.log("Error in api call",error);
      
    }finally{
      setloading(false);
    }
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Email Reply Generator
      </Typography>
      <Box sx={{ mX: 3 }}>
        <TextField
          fullWidth
          label="Original Email Content"
          multiline
          rows={6}
          variant="outlined"
          value={emailContent || ""}
          onChange={(e) => setEmailContent(e.target.value)}
          sx={{mb:2}}
        />
        <FormControl fullWidth sx={{mb:2}}>
        <InputLabel>Tone(Optional)</InputLabel>
        <Select
          value={tone || ''}
          label="Tone (Optional)"
          onChange={(e)=> setTone(e.target.value)}
        >
          <MenuItem value="">None</MenuItem>
          <MenuItem value="professional">Professional</MenuItem>
          <MenuItem value="casual">Casual</MenuItem>
          <MenuItem value="friendly">Friendly</MenuItem>
        </Select>
      </FormControl>

      <Button variant="contained" onClick={handleSubmit} disabled ={!emailContent || loading} sx={{mb:2}}>
        {loading ? <CircularProgress size={24}/> : "Genccerate Reply"}
      </Button>
      </Box>

      <Box sx={{ mX: 3 }}>
        <TextField
          fullWidth
          label="Original Email Content"
          multiline
          rows={6}
          variant="outlined"
          value={generatedReply || ""}
          inputProps={{readonly : true}}
          sx={{mb:2}}
          
        />

        <Button variant="outlined"
        onClick={()=> navigator.clipboard.write(generatedReply)} sx={{mb:2}} >
          Copy to Clipboard
        </Button>
        </Box>
      
    </Container>
  );
}

export default App;
