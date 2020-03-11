import {IAttribut} from "app/shared/model/attribut.model";
import axios from "axios";

export const loadAttributs = (familyId: number, handlingChange: (entities: IAttribut[]) => void) => {
  const apiUrl = 'api/families';
  axios.get<IAttribut[]>(`${apiUrl}/familyAttributs/${familyId}`)
    .then(response => handlingChange(response.data))
};
