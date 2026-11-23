# READE.md

## Registro cronológico de comandos Git

### 1. Comprobación inicial de ramas y archivos

```powershell
git branch -a
git switch frontend
ls
git switch backend
ls
git switch main
ls
git branch
```

### 2. Comprobación y eliminación del archivo Java duplicado

```powershell
git status
git ls-files
Remove-Item main.java
git status
git add main.java
git commit -m "Eliminar archivo Java duplicado"
git status
git ls-files
```

### 3. Corrección del commit realizado en main

```powershell
git reset --mixed HEAD~1
git restore main.java
```

### 4. Comprobación del contenido de las ramas

```powershell
git ls-tree -r --name-only backend
git ls-tree -r --name-only frontend
git ls-tree -r --name-only main
git status
```

### 5. Actualización de la rama backend

```powershell
git switch backend
Compare-Object (Get-Content .\main.java) (Get-Content .\CalculadoraServer.java)
git rm main.java
git add CalculadoraServer.java
git commit -m "Renombrar clase principal de la calculadora"
git status
git ls-tree -r --name-only backend
```

### 6. Comprobación de frontend y main

```powershell
git switch frontend
git ls-tree -r --name-only frontend
git status
git switch main
git ls-tree -r --name-only main
git status
```

### 7. Comprobación del historial y corrección de README

```powershell
git diff -- README.md
git log --oneline --graph --all -8
git restore README.md
```

### 8. Integración final de backend en main

```powershell
git switch backend
git status
git switch main
git merge backend
git ls-tree -r --name-only main
git status