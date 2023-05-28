import cv2

def carregar_imagem(img_path):
    img = cv2.imread(img_path, cv2.IMREAD_ANYCOLOR)
    return img