import json
import os
import re

from docling.document_converter import DocumentConverter
from docling.exceptions import ConversionError
from pathlib import Path


# Build paths inside the project like this: BASE_DIR / 'subdir'.
BASE_DIR = Path(__file__).resolve().parent.parent

converter = DocumentConverter()

conversions = 0

def convert_pdf_to_plain_text(source, file_name):
    file_name_txt = f"{BASE_DIR.parent}/politics-agents/conversor-pdf-to-plain-text/proposals_txt/{file_name}.txt"
    
    if source is not None:
        if not os.path.exists(file_name_txt):
            try:
                print(f'source: {source}')
                print(f'file name: {file_name}')
                global conversions
                # source = "https://www.camara.leg.br/proposicoesWeb/prop_mostrarintegra?codteor=585911"  # document per local path or URL
                result = converter.convert(source, max_num_pages=5)

                # print(result.document.export_to_text())

                text = result.document.export_to_text()

            
                with open(file_name_txt, "w", encoding="utf-8") as file:
                    file.write(text)

                print(f"O arquivo '{file_name_txt}' foi salvo com sucesso!")
                conversions = conversions + 1
            except ConversionError as e:
                print(f"Erro ao converter o PDF '{source}': {e}")
        else:
            print(f"O arquivo '{file_name_txt}' já existe!")
    else:
        print(f"Proposição {file_name} não possui source")

def get_pdf_link(json_name):
    json_path = f"/home/douglas/Documentos/www/politics-agents/external-data-processor/src/main/resources/proposicoes/{json_name}"

    with open(json_path, "r", encoding="utf-8") as json_file:
        data = json.load(json_file)

        # Exibe os dados carregados
        # print("Dados carregados do JSON:")
        # print(data)
        if data['dados'] and data['dados']['urlInteiroTeor']:
            return data['dados']['urlInteiroTeor']

    return None

def read_all_json_files():
    pattern = re.compile(r"^\d+\.json$")
    dir = "/home/douglas/Documentos/www/politics-agents/external-data-processor/src/main/resources/proposicoes/"
    content = os.listdir(dir)

    # Filtra apenas os arquivos
    jsons = [json for json in content if os.path.isfile(os.path.join(dir, json)) and pattern.match(json)]

    print("Arquivos encontrados:", len(jsons))
    print(jsons)
    return jsons

def main():
    global conversions
    jsons = read_all_json_files()

    for json_name in jsons:
        print(f'proposição: {json_name}')
        json_name_without_extension = json_name.removesuffix(".json")
        link = get_pdf_link(json_name)
        
        if link is not None:
            convert_pdf_to_plain_text(link, json_name_without_extension)

    print("conversões:")
    print(conversions)

if __name__ == '__main__':
    main()