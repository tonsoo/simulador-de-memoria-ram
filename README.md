# simulador-de-memoria-ram
Um simulador de memória ram desenvolvido para disciplide de Arquiterura e Organização de Computadores, lecionada no segundo período da minha faculdade

# Comandos do terminal:

/openfile -> Abre uma janela para a seleção do codigo com extensão ".n"
/closefile -> Fecha o arquivo que está aberto
/cls -> Limpa a tela do terminal
/writeenable -> Ativa/Desativa o write enabled da memoria ram

# Comandos do processador:

0 -> representação de valor inteiro decimal
(0) -> representação de endereço de memoria decimal
[f] -> representação de endereço de memoria hexadecimal
#reg -> representação de registrador
= -> representação de comparações

move 0|(0)|[f]|#reg para (0)|[f]|#reg -> Move um valor (inteiro, endereço de memoria ou registrador) para um endereco de memoria ou registrador

copia (0)|[f]|#reg para (0)|[f]|#reg -> Copia o valor de um endereco de memoria ou registrador para outro endereço de memoria ou registrador

add 0 -> Adiciona o valor inteiro do registrador #atx

sub 0 -> Subtrai um valor inteiro do registrador #atx

pula 0 -> Pula para a linha indicada

comp 0|(0)|[f]|#reg = 0|(0)|[f]|#reg -> Faz uma comparação e ira executar o proximo comando apenas se o resultado da comparação for 1 (verdadeiro)



# Nome dos registradores:

#atx
#etx
#itx
#otx
