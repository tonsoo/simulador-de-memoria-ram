# Terminal:

**/openfile**\
Abre uma janela para a seleção de um arquivo de codigo com extensão ".n".

**/closefile**\
Fecha o arquivo que está aberto.

**/cls**\
Limpa a tela do terminal.

**/writeenable**\
Ativa/desativa a permissão de escrita da memória RAM.

# Comandos do processador:

### Representação de valores

**(int)** Valor inteiro decimal: `0`\
**(addr)** Endereço de memória decimal: `(0)`\
**(addr)** Endereço de memória hexadecimal: `[f]`\
**(reg)** Representação de registrador: `#atx`, `#etx`, `#itx`, `#otx`

### Conjunto de Instruções (Instruction Set)

`move <int|addr|reg> para <addr|reg>`\
Move um valor inteiro, ou um valor armazenado em um endereço de memória ou registrador, para um endereco de memória ou registrador.

`copia <int|addr|reg> para <addr|reg>`\
Copia o valor de um endereco de memoria ou registrador para outro endereço de memória ou registrador.

`add <int>`\
Adiciona o um valor inteiro do registrador `#atx`.

`sub <int>`\
Subtrai um valor inteiro do registrador `#atx`.

`pula <int>`\
Pula para a linha indicada.

`comp <int|addr|reg> = <int|addr|reg>`
Faz uma comparação e ira executar o proximo comando apenas se o resultado da comparação for 1 (verdadeiro).

# Registradores:

`#atx`: Usado para aritimetica.\
`#etx`\
`#itx`\
`#otx`