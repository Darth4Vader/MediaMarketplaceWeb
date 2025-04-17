import {ComponentPreview, Previews} from '@react-buddy/ide-toolbox'
import {PaletteTree} from './palette'
import App from "../App";
import LoginPage from "../LoginPage";

const ComponentPreviews = () => {
    return (
        <Previews palette={<PaletteTree/>}>
            <ComponentPreview path="/App">
                <App/>
            </ComponentPreview>
            <ComponentPreview path="/LoginPage">
                <LoginPage/>
            </ComponentPreview>
        </Previews>
    )
}

export default ComponentPreviews